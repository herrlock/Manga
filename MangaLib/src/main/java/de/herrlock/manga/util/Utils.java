package de.herrlock.manga.util;

import static de.herrlock.manga.util.Constants.TO_DOCUMENT_HANDLER;

import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.management.ManagementFactory;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import javax.management.InstanceAlreadyExistsException;
import javax.management.MBeanRegistrationException;
import javax.management.MBeanServer;
import javax.management.MalformedObjectNameException;
import javax.management.NotCompliantMBeanException;
import javax.management.ObjectName;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;

import com.google.common.base.Joiner;
import com.google.common.util.concurrent.ThreadFactoryBuilder;

import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.util.configuration.Configuration.ProxyStorage;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * A central class for static methods
 * 
 * @author HerrLock
 */
public final class Utils {
    private static final Logger logger = LogManager.getLogger();
    // private static final Logger debugLogger = LogManager.getLogger( "com.example.java.debug.Utils" );

    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool( 20,
        new ThreadFactoryBuilder().setNameFormat( "Droggelb%dcher" ).setDaemon( true ).build() );
    private static final HttpClient CLIENT = HttpClients.createDefault();
    private static final Joiner COMMA_JOINER = Joiner.on( "," );

    /**
     * Creates new {@link HttpGet} to the given {@link URL} and with the given {@link DownloadConfiguration}
     * 
     * @param url
     *            the {@link URL} to connect to
     * @param conf
     *            a {@link DownloadConfiguration} containing the parameters to use
     * @return a new {@link HttpGet}
     */
    public static HttpGet createHttpGet( final URL url, final DownloadConfiguration conf ) {
        HttpGet get = new HttpGet( url.toExternalForm() );
        int timeout = conf.getTimeout();
        ProxyStorage proxy = conf.getProxy();
        HttpHost proxyHost = proxy.getHttpHost();
        RequestConfig config = RequestConfig.custom() //
            .setConnectTimeout( timeout ) //
            .setSocketTimeout( timeout ) //
            .setProxy( proxyHost ) //
            .build();
        get.setConfig( config );
        String creds = proxy.getCreds();
        if ( creds != null ) {
            get.setHeader( HttpHeaders.PROXY_AUTHORIZATION, "Basic " + creds );
        }
        return get;
    }

    /**
     * Executes the given {@link HttpGet} with a {@link CloseableHttpClient}
     * 
     * @param <T>
     *            the result-type of the ResponseHandler
     * @param httpGet
     *            the {@link HttpGet} to execute
     * @param handler
     *            the {@link ResponseHandler} to process the result with
     * @return the result of the {@link ResponseHandler}
     * @throws IOException
     *             thrown by {@link CloseableHttpClient#execute(org.apache.http.client.methods.HttpUriRequest)}
     * @throws ClientProtocolException
     *             thrown by {@link CloseableHttpClient#execute(org.apache.http.client.methods.HttpUriRequest)}
     */
    public static <T> T executeHttpGet( final HttpGet httpGet, final ResponseHandler<T> handler )
        throws IOException, ClientProtocolException {
        return CLIENT.execute( httpGet, handler );
    }

    /**
     * Creates a {@link HttpGet} with {@link #createHttpGet(URL, DownloadConfiguration)} and executes it with
     * {@link #executeHttpGet(HttpGet, ResponseHandler)}
     * 
     * @param <T>
     *            the result-type of the ResponseHandler
     * @param url
     *            the {@link URL} to connect to
     * @param conf
     *            a {@link DownloadConfiguration} containing the parameters to use
     * @param handler
     *            the {@link ResponseHandler} to process the result with
     * @return the result of the {@link ResponseHandler}
     * @throws IOException
     *             thrown by {@link CloseableHttpClient#execute(org.apache.http.client.methods.HttpUriRequest)}
     * @throws ClientProtocolException
     *             thrown by {@link CloseableHttpClient#execute(org.apache.http.client.methods.HttpUriRequest)}
     */
    public static <T> T getDataAndExecuteResponseHandler( final URL url, final DownloadConfiguration conf,
        final ResponseHandler<T> handler ) throws IOException, ClientProtocolException {
        logger.debug( "url: {}", url );
        final HttpGet httpGet = createHttpGet( url, conf );
        return executeHttpGet( httpGet, handler );
    }

    public static Document getDocument( final URL url, final DownloadConfiguration conf )
        throws ClientProtocolException, IOException {
        return getDataAndExecuteResponseHandler( url, conf, TO_DOCUMENT_HANDLER );
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
