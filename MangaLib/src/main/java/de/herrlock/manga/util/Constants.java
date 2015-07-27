package de.herrlock.manga.util;

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
     * the name of the property to set a custom timeout with
     */
    public static final String PARAM_TIMEOUT = "timeout";
    /**
     * the path to JDownloader's folderwatch-folder
     */
    public static final String PARAM_JDFW = "jdfw";
    /**
     * the default value of th timeout
     */
    public static final int PARAM_TIMEOUT_DEFAULT = 5_000;
    /**
     * the name of the property for setting the proxy-url
     */
    public static final String PARAM_PROXY = "proxy";

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
        public int compare( final String s1, final String s2 ) {
            final double d1 = Double.parseDouble( s1 );
            final double d2 = Double.parseDouble( s2 );
            return Double.compare( d1, d2 );
        }
    };

    private Constants() {
        // not called
    }
}
