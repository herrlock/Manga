package de.herrlock.manga.util;

import java.io.IOException;
import java.io.InputStream;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Collection;

import org.apache.commons.io.IOUtils;
import org.apache.commons.io.output.ByteArrayOutputStream;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.herrlock.manga.util.configuration.DownloadConfiguration;

public final class Utils {

    /**
     * creates a connection with the given {@linkplain DownloadConfiguration}
     * 
     * @param url
     *            the {@link URL} to create a connection from
     * @param conf
     *            the {@link DownloadConfiguration} to create a connection with
     * @return a {@link URLConnection} from the given {@link URL}
     * @throws IOException
     *             if an I/O exception occurs.
     */
    public static URLConnection getConnection( final URL url, final DownloadConfiguration conf ) throws IOException {
        final Proxy proxy = conf.getProxy();
        final URLConnection con = url.openConnection( proxy );
        con.setConnectTimeout( conf.getTimeout() );
        con.setReadTimeout( 2 * conf.getTimeout() );
        return con;
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
        final URLConnection con = getConnection( url, conf );
        final ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try ( final InputStream in = con.getInputStream() ) {
            IOUtils.copy( in, baos );
        }
        final String result = baos.toString( StandardCharsets.UTF_8.name() );
        return Jsoup.parse( result );
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
}
