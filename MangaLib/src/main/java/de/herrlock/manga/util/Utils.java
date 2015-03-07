package de.herrlock.manga.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.herrlock.manga.connection.ConnectionFactory;
import de.herrlock.manga.connection.DirectConnectionFactory;
import de.herrlock.manga.connection.ProxyConnectionFactory;

public final class Utils {

    /**
     * the required properties
     */
    private static final List<String> requiredParameters = Collections.unmodifiableList( Arrays.asList( Constants.PARAM_URL ) );

    /**
     * the current arguments
     */
    private static Map<String, String> arguments;
    /**
     * the active ConnectionFactory
     */
    private static ConnectionFactory conFactory;

    /**
     * @return the current arguments
     */
    public static Map<String, String> getArguments() {
        if ( arguments == null ) {
            throw new RuntimeException( "arguments not yet initialized" );
        }
        return arguments;
    }

    /**
     * sets {@link Utils#arguments} to the values from the given {@link Properties}<br>
     * validates the properties for required parameters, in case a required parameter is missing en exception is thrown
     * 
     * @param p
     *            the Properties to set as current arguments
     */
    public static void setArguments( Properties p ) {
        // check arguments for required parameters
        for ( String s : requiredParameters ) {
            String value = p.getProperty( s );
            if ( value == null || "".equals( value ) ) {
                throw new RuntimeException( "Please fill the field \"" + s + "\" in the file "
                    + new File( Constants.SETTINGS_FILE ).getAbsolutePath() );
            }
        }

        // copy Properties to Map
        Map<String, String> m = new ConcurrentHashMap<>();
        for ( String s : p.stringPropertyNames() ) {
            m.put( s, p.getProperty( s ) );
        }
        arguments = Collections.unmodifiableMap( m );

        // create a new ConnectionFactory depending on the proxy-parameters
        String host = m.get( Constants.PARAM_PROXY_HOST );
        String port = m.get( Constants.PARAM_PROXY_PORT );
        String timeout = m.get( Constants.PARAM_TIMEOUT );
        boolean proxyHostAvailabile = host != null && !"".equals( host );
        boolean proxyPortAvailabile = port != null && !"".equals( port );
        if ( proxyHostAvailabile && proxyPortAvailabile ) {
            conFactory = new ProxyConnectionFactory( timeout, host, port );
        } else {
            conFactory = new DirectConnectionFactory( timeout );
        }
    }

    /**
     * creates a connection
     * 
     * @param url
     *            the url to create a connection from
     * @return a {@link URLConnection} from the given {@link URL}
     * @throws IOException
     *             if an I/O exception occurs.
     * @see ConnectionFactory#getConnection(URL)
     */
    public static URLConnection getConnection( URL url ) throws IOException {
        return conFactory.getConnection( url );
    }

    /**
     * fetches data from the given URL and parses it to a {@link Document}
     * 
     * @param url
     *            the URL to read from
     * @return a document, parsed from the given URL
     * @throws IOException
     */
    public static Document getDocument( URL url ) throws IOException {
        URLConnection con = getConnection( url );
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
     *             If an I/O error occurs
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

    public static void writeToFile( File file, List<String> readLines ) throws IOException {
        try ( PrintWriter pw = new PrintWriter( file, "UTF-8" ) ) {
            for ( String s : readLines ) {
                pw.println( s );
            }
        }
    }

    /**
     * returns the URL from the current arguments, optional appended to {@code http://}
     * 
     * @return a proper URL
     * @throws RuntimeException
     *             in case of a {@link MalformedURLException}
     */
    public static URL getMangaURL() {
        try {
            String _url = arguments.get( Constants.PARAM_URL );
            if ( !_url.startsWith( "http" ) ) {
                // prepend http://, in case it is not present
                _url = "http://" + _url;
            }
            return new URL( _url );
        } catch ( MalformedURLException ex ) {
            throw new RuntimeException( ex );
        }
    }

    /**
     * gets the pattern from the current arguments
     * 
     * @return the entry with the key from {@link Constants#PARAM_PATTERN} (
     *         {@value de.herrlock.manga.util.Constants#PARAM_PATTERN})
     */
    public static String getPattern() {
        return arguments.get( Constants.PARAM_PATTERN );
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
