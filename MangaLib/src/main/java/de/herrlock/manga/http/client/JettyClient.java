package de.herrlock.manga.http.client;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

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

import de.herrlock.manga.util.configuration.DownloadConfiguration;

public class JettyClient {
    private static final Logger logger = LogManager.getLogger();

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
    }

    /**
     * Starts the HttpClient
     * 
     * @see #stopHttpClient()
     */
    public static void startHttpClient() {
        synchronized ( CLIENT ) {
            try {
                CLIENT.start();
            } catch ( Exception e ) {
                throw new RuntimeException( e );
            }
        }
    }

    /**
     * Stops the HttpClient
     * 
     * @see #startHttpClient()
     */
    public static void stopHttpClient() {
        synchronized ( CLIENT ) {
            try {
                CLIENT.stop();
            } catch ( Exception e ) {
                throw new RuntimeException( e );
            }
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
        startHttpClient();
        Request request = CLIENT.newRequest( uri ) //
            .timeout( conf.getTimeout(), TimeUnit.MILLISECONDS ) //
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
        return getDataAndExecuteResponseHandler( url, conf, new ToDocumentOrRetryHandler( url, conf ) );
    }

}
