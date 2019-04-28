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
     * a {@link Comparator} to compare Strings based on their numeric value
     */
    public static final Comparator<String> STRING_NUMBER_COMPARATOR = Comparator.comparingDouble( Double::parseDouble );

    /**
     * the average filesize in kB.<br>
     * estimated from :
     * <dl>
     * <dt>700 chapters of Naruto from Mangapanda</dt>
     * <dd>156.6 kB</dd>
     * <dt>799 chapters of OnePiece from Mangapanda</dt>
     * <dd>183.7 kB</dd>
     * <dt>700 + some coloured chapters of Naruto from Mangafox</dt>
     * <dd>139.2 kB</dd>
     * <dt>799 chapters of OnePiece from Mangafox</dt>
     * <dd>230.6 kB</dd>
     * </dl>
     */
    public static final int AVG_SIZE = 177;

    /**
     * unused constructor to avoid instantiation
     */
    private Constants() {
        // not called
    }
}
