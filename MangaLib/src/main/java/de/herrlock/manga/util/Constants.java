package de.herrlock.manga.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Comparator;

public final class Constants {
    public static final boolean DEBUG = Math.random() < 2;

    public static final String SETTINGS_FILE = "./downloader.properties";
    public static final String PARAM_URL = "url";
    public static final String PARAM_PATTERN = "pattern";
    public static final String PARAM_LOGLEVEL = "loglevel";
    public static final String PARAM_FILEPATH = "filepath";
    public static final String PARAM_TIMEOUT = "timeout";
    public static final int PARAM_TIMEOUT_DEFAULT = 5_000;

    public static final String PARAM_PROXY_HOST = "proxyHost";
    public static final String PARAM_PROXY_PORT = "proxyPort";
    public static final String PARAM_PROXY_USER = "proxyUser";
    public static final String PARAM_PROXY_PASS = "proxyPass";

    public static final String TARGET_FOLDER = "./download";
    public static final String TRACE_FILE = "./log/trace.log";

    public static final Comparator<String> STRING_NUMBER_COMPARATOR = new Comparator<String>() {
        @Override
        public int compare( String s1, String s2 ) {
            double d1 = Double.parseDouble( s1 );
            double d2 = Double.parseDouble( s2 );
            return Double.compare( d1, d2 );
        }
    };

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
