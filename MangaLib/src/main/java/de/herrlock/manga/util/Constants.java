package de.herrlock.manga.util;

import java.util.Comparator;

/**
 * A central class for miscellanous constant values
 * 
 * @author HerrLock
 */
public final class Constants {
    /**
     * the position of the configuration-file to runtime
     */
    public static final String SETTINGS_FILE = "./downloader.txt";
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
