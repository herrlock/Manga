package de.herrlock.manga.util;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadFactory;

import org.apache.http.HttpHost;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import de.herrlock.manga.exceptions.MyException;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * A central class for static methods
 * 
 * @author HerrLock
 */
public final class Utils {

    private static final ExecutorService THREAD_POOL = Executors.newFixedThreadPool( 20, new DaemonThreadFactory() );
    private static final CloseableHttpClient CLIENT = HttpClients.createDefault();

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
        HttpHost proxy = conf.getProxy();
        RequestConfig config = RequestConfig.custom().setConnectTimeout( timeout ).setSocketTimeout( timeout ).setProxy( proxy )
            .build();
        get.setConfig( config );
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
        final HttpGet httpGet = createHttpGet( url, conf );
        return executeHttpGet( httpGet, handler );
    }

    /**
     * start all {@link Thread}s in the give collection and wait for them to die
     * 
     * @param threads
     *            the {@link Thread}s to start and wait for
     * @throws MyException
     *             in case of an {@link InterruptedException}
     * @deprecated use {@link #callCallables(Collection)} instead
     */
    @Deprecated
    public static void startAndWaitForThreads( final Collection<? extends Thread> threads ) throws MyException {
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
            throw new MyException( ex );
        }
    }

    /**
     * Invokes all given Callables
     * 
     * @param callables
     *            the {@link Callable}s to execute
     * @return the result of {@link ExecutorService#invokeAll(Collection)}
     */
    public static <T> List<Future<T>> callCallables( final Collection<? extends Callable<T>> callables ) {
        try {
            return THREAD_POOL.invokeAll( callables );
        } catch ( final InterruptedException ex ) {
            throw new MyException( ex );
        }
    }

    private Utils() {
        // not called
    }

    /**
     * creates new Threads with the given ThreadFactory and marks them as daemon-threads
     * 
     * @author HerrLock
     */
    private static final class DaemonThreadFactory implements ThreadFactory {
        private final ThreadFactory threadFactory;

        /**
         * uses {@link Executors#defaultThreadFactory()} as {@link ThreadFactory}
         */
        public DaemonThreadFactory() {
            this( Executors.defaultThreadFactory() );
        }

        /**
         * @param threadFactory
         *            the {@link ThreadFactory} to use
         */
        public DaemonThreadFactory( final ThreadFactory threadFactory ) {
            this.threadFactory = threadFactory;
        }

        @Override
        public Thread newThread( final Runnable r ) {
            Thread t = this.threadFactory.newThread( r );
            t.setDaemon( true );
            return t;
        }
    }
}
