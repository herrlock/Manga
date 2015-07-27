package de.herrlock.manga.util;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.apache.http.HttpEntity;
import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.herrlock.manga.ui.LogWindow;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

public final class Utils {
    public static final LogWindow LOG = new LogWindow();

    private static final CloseableHttpClient client = HttpClients.createDefault();

    static {
        LOG.show();
    }

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
     * fetches data from the given URL and parses it to a {@link Document}
     * 
     * @param url
     *            the {@link URL} to read from
     * @param conf
     *            the conf with the URL to read from
     * @return a document, parsed from the given URL
     * @throws IOException
     */
    public static Document getDocument( final URL url, final DownloadConfiguration conf ) throws IOException {
        return getDataAndExecuteResponseHandler( url, conf, ResponseHandlers.TO_DOCUMENT_HANDLER );
    }

    public static <T> T getDataAndExecuteResponseHandler( final URL url, final DownloadConfiguration conf,
        ResponseHandler<T> handler ) throws IOException {
        final HttpGet httpGet = createHttpGet( url, conf );
        return client.execute( httpGet, handler );
    }

    /**
     * start all {@link Thread}s in the give collection and wait for them to die
     * 
     * @param threads
     *            the {@link Thread}s to start and wait for
     * @throws RuntimeException
     *             in case of an {@link InterruptedException}
     * @see Thread#start()
     * @see Thread#join()
     */
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
        } catch ( InterruptedException ex ) {
            throw new RuntimeException( ex );
        }
    }

    private Utils() {
        // not called
    }

    public static class ResponseHandlers {
        public static final ResponseHandler<String> TO_STRING_HANDLER = new ResponseHandler<String>() {
            @Override
            public String handleResponse( HttpResponse response ) throws IOException {
                HttpEntity entity = response.getEntity();
                try {
                    return EntityUtils.toString( entity, StandardCharsets.UTF_8 );
                } finally {
                    EntityUtils.consume( entity );
                }
            }
        };

        public static final ResponseHandler<Document> TO_DOCUMENT_HANDLER = new ResponseHandler<Document>() {
            @Override
            public Document handleResponse( HttpResponse response ) throws ClientProtocolException, IOException {
                String result = TO_STRING_HANDLER.handleResponse( response );
                return Jsoup.parse( result );
            }
        };

        private ResponseHandlers() {
            // not used
        }
    }

}
