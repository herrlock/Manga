package de.herrlock.manga.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.management.ManagementFactory;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.client.HttpClient;
import org.eclipse.jetty.client.HttpProxy;
import org.eclipse.jetty.client.ProxyConfiguration.Proxy;
import org.eclipse.jetty.client.api.Authentication;
import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.eclipse.jetty.client.util.BasicAuthentication;
import org.eclipse.jetty.util.ssl.SslContextFactory;
import org.jsoup.nodes.Document;

import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.exceptions.ResponseHandlerException;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * A central class for static methods
 * 
 * @author HerrLock
 */
public final class Utils {
    private static final Logger logger = LogManager.getLogger();
    // private static final Logger debugLogger = LogManager.getLogger( "com.example.java.debug.Utils" );

    private static final Joiner COMMA_JOINER = Joiner.on( "," );
    private static final ExecutorService THREAD_POOL;
    private static final HttpClient CLIENT;
    private static final String USER_AGENT;

    static {
        final Properties p = new Properties();

        Path confFile = Paths.get( "conf", "settings.conf" );
        if ( Files.exists( confFile ) ) {
            try ( InputStream in = Files.newInputStream( confFile ) ) {
                p.load( in );
            } catch ( IOException ex ) {
                throw new RuntimeException( ex );
            }
        }

        int count = Integer.parseInt( p.getProperty( "thread.count", "10" ) );
        THREAD_POOL = Executors.newFixedThreadPool( count,
            new ThreadFactoryBuilder().setNameFormat( "Droggelb%03dcher" ).setDaemon( true ).build() );

        USER_AGENT = p.getProperty( "http.useragent" );

        boolean trustAll = Boolean.parseBoolean( p.getProperty( "https.trustAll", "false" ) );
        SslContextFactory sslCF = new SslContextFactory.Client( trustAll );
        CLIENT = new HttpClient( sslCF );

        String proxyHost = p.getProperty( "http.proxy.host" );
        String proxyPort = p.getProperty( "http.proxy.port", "-1" );
        if ( proxyHost != null && !"-1".equals( proxyPort ) ) {
            int proxyPortInt = Integer.parseInt( proxyPort );
            Proxy proxy = new HttpProxy( proxyHost, proxyPortInt );
            CLIENT.getProxyConfiguration().getProxies().add( proxy );

            String proxyUser = p.getProperty( "http.proxy.user" );
            String proxyPassword = p.getProperty( "http.proxy.password" );
            String proxyRealm = p.getProperty( "http.proxy.realm" );
            if ( proxyUser != null && proxyPassword != null && proxyRealm != null ) {
                URI proxyURI = URI.create( proxyHost + ":" + proxyPort );
                Authentication proxyAuthentication = new BasicAuthentication( proxyURI, proxyRealm, proxyUser, proxyPassword );
                CLIENT.getAuthenticationStore().addAuthentication( proxyAuthentication );
            }
        }

        try {
            CLIENT.start();
        } catch ( Exception e ) {
            throw new RuntimeException( e );
        }
    }

    /**
     * Creates new {@link Request} to the given {@link URL} and with the given {@link DownloadConfiguration}
     * 
     * @param url
     *            the {@link URL} to connect to
     * @param conf
     *            a {@link DownloadConfiguration} containing the parameters to use
     * @return a new {@link Request}
     */
    public static Request createHttpGet( final URL url, final DownloadConfiguration conf ) {
        URI uri;
        try {
            uri = url.toURI();
        } catch ( URISyntaxException ex ) {
            throw new RuntimeException( ex );
        }
        Request request = CLIENT.newRequest( uri ) //
            .timeout( conf.getTimeout(), TimeUnit.SECONDS ) //
            .agent( USER_AGENT );

        return request;
    }

    /**
     * Creates a {@link Request} with {@link #createHttpGet(URL, DownloadConfiguration)} and executes it.
     * 
     * @param <T>
     *            the result-type of the ResponseHandler
     * @param url
     *            the {@link URL} to connect to
     * @param conf
     *            a {@link DownloadConfiguration} containing the parameters to use
     * @param handler
     *            the {@link ResponseHandler} to process the result with
     * @return the result of the handler
     * @throws IOException
     *             thrown when {@link Request#send()} throws an Exception
     */
    public static <T> T getDataAndExecuteResponseHandler( final URL url, final DownloadConfiguration conf,
        final ResponseHandler<T> handler ) throws IOException {
        logger.debug( "url: {}", url );
        final Request request = createHttpGet( url, conf );
        try {
            ContentResponse response = request.send();
            return handler.apply( response );
        } catch ( InterruptedException | TimeoutException | ExecutionException ex ) {
            throw new IOException( ex );
        }
    }

    public static Document getDocument( final URL url, final DownloadConfiguration conf ) throws IOException {
        return getDataAndExecuteResponseHandler( url, conf, new ToDocumentHandler( url, conf ) );
    }

    /**
     * converts an {@link ContentResponse} to a Jsoup-{@link Document}
     */
    public static final class ToDocumentHandler extends ResponseHandler<Document> {

        private final URL url;
        private final DownloadConfiguration conf;

        public ToDocumentHandler( final URL url, final DownloadConfiguration conf ) {
            this.url = url;
            this.conf = conf;
        }

        @Override
        public Document handle( final ContentResponse response ) {
            if ( response == null ) {
                throw new IllegalArgumentException( "ContentResponse is null" );
            }
            int statusCode = response.getStatus();
            switch ( statusCode ) {
                case 200:
                    return Constants.TO_DOCUMENT_HANDLER.apply( response );
                case 503:
                    try {
                        Thread.sleep( 1000 );
                    } catch ( InterruptedException ex ) {
                        throw new ResponseHandlerException( ex );
                    }
                    try {
                        return Utils.getDataAndExecuteResponseHandler( this.url, this.conf, this );
                    } catch ( IOException ex ) {
                        throw new ResponseHandlerException( ex );
                    }
                default:
                    throw new ResponseHandlerException( "Received non-expected StatusCode: " + statusCode );
            }
        }
    }

    /**
     * start all {@link Thread}s in the give collection and wait for them to die
     * 
     * @param threads
     *            the {@link Thread}s to start and wait for
     * @throws MDRuntimeException
     *             in case of an {@link InterruptedException}
     * @deprecated use {@link #callCallables(Collection)} instead
     */
    @Deprecated
    public static void startAndWaitForThreads( final Collection<? extends Thread> threads ) {
        // start all threads
        for ( Thread t : threads ) {
            t.start();
        }
        // wait for the threads to terminate
        try {
            for ( Thread t : threads ) {
                t.join();
            }
        } catch ( final InterruptedException ex ) {
            throw new MDRuntimeException( ex );
        }
    }

    /**
     * Queue all given Callables in the ExecutorService and returns the resulting {@link Future}s
     * 
     * @param <T>
     *            The generic type of the given callables, the generic type of the Future-objects in the result
     * @param callables
     *            the {@link Callable}s to execute
     * @return the result of {@link ExecutorService#invokeAll(Collection)}
     */
    public static <T> List<Future<T>> callCallables( final Collection<? extends Callable<T>> callables ) {
        try {
            return THREAD_POOL.invokeAll( callables );
        } catch ( final InterruptedException ex ) {
            throw new MDRuntimeException( ex );
        }
    }

    /**
     * Queue the Callable in the ExecutorService and returns the resulting {@link Future}.
     * 
     * @param callable
     *            the Callable to run
     * @param <T>
     *            result-type of the passed Callable
     * @return the Future that results from this Callable
     * @see ExecutorService#submit(Callable)
     */
    public static <T> Future<T> callCallable( final Callable<T> callable ) {
        return THREAD_POOL.submit( callable );
    }

    /**
     * Queue the Runnable in the ExecutorService and returns the resulting {@link Future}.
     * 
     * @param runnable
     *            the Runnable to run
     * @return the Future always returns {@code null}
     * @see ExecutorService#submit(Runnable)
     */
    public static Future<?> callCallable( final Runnable runnable ) {
        return THREAD_POOL.submit( runnable );
    }

    public static PropertiesBuilder newPropertiesBuilder() {
        return new PropertiesBuilder();
    }

    public static PropertiesBuilder newPropertiesBuilder( final Properties defaults ) {
        return new PropertiesBuilder( defaults );
    }

    /**
     * Register the MBean at the ObjectName constructed by the given Strings.
     * 
     * @param mbean
     *            The MBean to register.
     * @param domain
     *            The domain for the ObjectName.
     * @param params
     *            An array of parameters. These parameters are joined by a comma ({@code ,}) to represent the path of the
     *            ObjectName.
     * @see #registerMBean(Object, String, String)
     */
    public static void registerMBean( final Object mbean, final String domain, final String... params ) {
        String paramString = COMMA_JOINER.join( params );
        registerMBean( mbean, domain, paramString );
    }

    /**
     * Registers the given MBean at the ObjectName constructed by the given Strings. The domain and the path are concatenated by a
     * colon ({@code :}) to represent the ObjectName.
     * 
     * @param mbean
     *            The MBean to register.
     * @param domain
     *            The domain for the ObjectName.
     * @param paramString
     *            The path of the ObjectName.
     * @see #registerMBean(Object, String)
     */
    public static void registerMBean( final Object mbean, final String domain, final String paramString ) {
        String name = domain + ":" + paramString;
        registerMBean( mbean, name );
    }

    /**
     * Registers the given MBean at the ObjectName constructed by the given String.
     * 
     * @param mbean
     *            The MBean to register.
     * @param name
     *            The String representing the ObjectName of the MBean. Passed to {@link ObjectName#ObjectName(String) new
     *            ObjectName(String)}.
     */
    public static void registerMBean( final Object mbean, final String name ) {
        try {
            ObjectName objectName = new ObjectName( name );
            registerMBean( mbean, objectName );
        } catch ( MalformedObjectNameException ex ) {
            logger.warn( "Could not register MBeans, ignoring this.", ex );
        }
    }

    /**
     * Registers the given MBean at the given ObjectName.
     * 
     * @param mbean
     *            The MBean to register.
     * @param name
     *            The ObjectName of the MBean.
     */
    public static void registerMBean( final Object mbean, final ObjectName name ) {
        try {
            MBeanServer mbs = ManagementFactory.getPlatformMBeanServer();
            mbs.registerMBean( mbean, name );
        } catch ( InstanceAlreadyExistsException | MBeanRegistrationException | NotCompliantMBeanException ex ) {
            logger.warn( "Could not register MBeans, ignoring this.", ex );
        }
    }

    private Utils() {
        // not called
    }

    public static abstract class ResponseHandler<T> implements Function<ContentResponse, T> {

        protected abstract T handle( ContentResponse input ) throws IOException;

        @Override
        public final T apply( final ContentResponse input ) throws ResponseHandlerException {
            try {
                return handle( input );
            } catch ( IOException ex ) {
                throw new ResponseHandlerException( ex );
            }
        }
    }

    public static class PropertiesBuilder {
        private final Properties _properties;
        private boolean open = true;

        public PropertiesBuilder() {
            this._properties = new Properties();
        }

        public PropertiesBuilder( final Properties defaults ) {
            this._properties = new Properties( defaults );
        }

        private void assureOpen() {
            if ( !this.open ) {
                throw new IllegalStateException( "Builder is already finished" );
            }
        }

        public PropertiesBuilder setProperty( final String key, final String value ) {
            assureOpen();
            if ( key != null ) {
                if ( value == null ) {
                    if ( this._properties.containsKey( key ) ) {
                        this._properties.remove( key );
                    }
                } else {
                    this._properties.setProperty( key, value );
                }
            }
            return this;
        }

        public PropertiesBuilder load( final Reader reader ) throws IOException {
            assureOpen();
            this._properties.load( reader );
            return this;
        }

        public PropertiesBuilder load( final InputStream inStream ) throws IOException {
            assureOpen();
            this._properties.load( inStream );
            return this;
        }

        public Properties build() {
            assureOpen();
            this.open = false;
            return this._properties;
        }

    }
}
