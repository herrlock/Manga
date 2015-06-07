package de.herrlock.manga.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.herrlock.manga.util.configuration.DownloadConfiguration;

public final class Utils {

    /**
     * creates a connection
     * 
     * @param conf
     *            the DOwnloadonfiguration to create a connection from
     * @return a {@link URLConnection} from the given {@link URL}
     * @throws IOException
     *             if an I/O exception occurs.
     */
    public static URLConnection getConnection( URL url, DownloadConfiguration conf ) throws IOException {
        final Proxy proxy = conf.getProxy();
        final URLConnection con = url.openConnection( proxy );
        con.setConnectTimeout( Constants.PARAM_TIMEOUT_DEFAULT );
        con.setReadTimeout( 2 * Constants.PARAM_TIMEOUT_DEFAULT );
        return con;
    }

    /**
     * fetches data from the given URL and parses it to a {@link Document}
     * 
     * @param conf
     *            the conf with the URL to read from
     * @return a document, parsed from the given URL
     * @throws IOException
     */
    public static Document getDocument( URL url, DownloadConfiguration conf ) throws IOException {
        URLConnection con = getConnection( url, conf );
        List<String> list = readStream( con.getInputStream() );
        StringBuilder sb = new StringBuilder();
        for ( String s : list ) {
            sb.append( s );
        }
        return Jsoup.parse( sb.toString() );
    }

    /**
     * reads the given {@link InputStream} and stores the result in a {@link List}
     * 
     * @param in
     *            the {@link InputStream} to read from
     * @return a {@link List} of Strings containing the lines from the read InputStream
     * @throws IOException
     *             If an IOException occurs while reading from the stream or closing the stream
     */
    public static List<String> readStream( InputStream in ) throws IOException {
        List<String> list = new ArrayList<>();
        try ( BufferedReader reader = new BufferedReader( new InputStreamReader( in, StandardCharsets.UTF_8 ) ) ) {
            String nextline = null;
            while ( ( nextline = reader.readLine() ) != null ) {
                list.add( nextline );
            }
        }
        return list;
    }

    /**
     * writes the Strings from the second parameter to the File in the first parameter, separated by a newline-character
     * 
     * @param file
     *            the file to write to
     * @param readLines
     *            the String to write
     * @throws IOException
     *             if a FileNotFoundException occurs while creating the PrintWriter
     */
    public static void writeToFile( File file, List<String> readLines ) throws IOException {
        try ( PrintWriter pw = new PrintWriter( file, "UTF-8" ) ) {
            for ( String s : readLines ) {
                pw.println( s );
            }
        }
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
    public static void startAndWaitForThreads( Collection<? extends Thread> threads ) {
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
    public static void trace( Object message ) {
        Constants.TRACE.println( message );
    }

    private Utils() {
        // not called
    }
}
