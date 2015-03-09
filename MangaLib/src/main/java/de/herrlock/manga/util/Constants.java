package de.herrlock.manga.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;

public final class Constants {
    /**
     * the position of the configuration-file to runtime
     */
    public static final String SETTINGS_FILE = "./downloader.txt";
    /**
     * the name of the property for the url
     */
    public static final String PARAM_URL = "url";
    /**
     * the name of the property for the downloadpattern
     */
    public static final String PARAM_PATTERN = "pattern";
    /**
     * the name of the property for the path to store the downloads in
     */
    public static final String PARAM_FILEPATH = "filepath";
    /**
     * the name of the property to set a custom timeout with
     */
    public static final String PARAM_TIMEOUT = "timeout";
    /**
     * the default value of th timeout
     */
    public static final int PARAM_TIMEOUT_DEFAULT = 5_000;
    /**
     * the name of the property for setting the proxy-host
     */
    public static final String PARAM_PROXY_HOST = "proxyHost";
    /**
     * the name of the property for setting the proxy-port
     */
    public static final String PARAM_PROXY_PORT = "proxyPort";
    /**
     * the name of the property for setting the proxy-user
     */
    public static final String PARAM_PROXY_USER = "proxyUser";
    /**
     * the name of the property for setting the proxy-password
     */
    public static final String PARAM_PROXY_PASS = "proxyPass";

    /**
     * the default location to save the downloads into
     */
    public static final String TARGET_FOLDER = "./download";
    /**
     * the location of the trace-file
     */
    public static final String TRACE_FILE = "./log/trace.log";

    /**
     * a {@link Comparator} to compare Strings based on their numeric value
     */
    public static final Comparator<String> STRING_NUMBER_COMPARATOR = new Comparator<String>() {
        @Override
        public int compare( String s1, String s2 ) {
            double d1 = Double.parseDouble( s1 );
            double d2 = Double.parseDouble( s2 );
            return Double.compare( d1, d2 );
        }
    };

    /**
     * a PrintWriter to write tracing-messages with
     */
    public static final PrintWriter TRACE;
    static {
        try {
            TRACE = new PrintWriter( new OutputStreamWriter( new FileOutputStream( TRACE_FILE ), StandardCharsets.UTF_8 ), true );
        } catch ( FileNotFoundException ex ) {
            throw new RuntimeException( ex );
        }
    }

    private Constants() {
        // not called
    }
}
