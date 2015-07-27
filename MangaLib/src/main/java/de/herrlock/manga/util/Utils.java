package de.herrlock.manga.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
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

import de.herrlock.manga.util.configuration.DownloadConfiguration;

public final class Utils {

    private static final CloseableHttpClient client = HttpClients.createDefault();

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
        final HttpGet httpGet = createHttpGet( url, conf );
        return client.execute( httpGet, ResponseHandlers.TO_DOCUMENT_HANDLER );
    }

    public static void copyResponseToFile( final URL url, final DownloadConfiguration conf, File outputFile ) throws IOException {
        final HttpGet httpGet = createHttpGet( url, conf );
        client.execute( httpGet, ResponseHandlers.newCopyToFileHandler( outputFile ) );
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

    /**
     * trace an object<br>
     * calls {@link Constants#TRACE}{@code .}{@link java.io.PrintWriter#println(Object) println(Object)}
     * 
     * @param message
     *            the message to trace
     */
    public static void trace( final Object message ) {
        Constants.TRACE.println( message );
    }
    public static void trace( final Throwable t ) {
        Constants.TRACE.println( "---" );
        t.printStackTrace( Constants.TRACE );
        Constants.TRACE.println( "---" );
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

        public static ResponseHandler<Void> newCopyToFileHandler( final File outputFile ) {
            return new ResponseHandler<Void>() {
                @Override
                public Void handleResponse( HttpResponse response ) throws ClientProtocolException, IOException {
                    HttpEntity entity = response.getEntity();
                    try ( InputStream in = entity.getContent() ) {
                        FileUtils.copyInputStreamToFile( in, outputFile );
                    } finally {
                        EntityUtils.consume( entity );
                    }
                    return null;
                }
            };
        }

        private ResponseHandlers() {
            // not used
        }
    }
}
