package de.herrlock.manga.host;

import java.net.URL;
import java.text.MessageFormat;

/**
 * a class to store number and url of a single Chapter
 * 
 * @author HerrLock
 */
public final class Chapter {
    private final String number;
    private final URL chapterUrl;

    /**
     * @param number
     *            tha Chapter's number. can contain alphanumerical characters
     * @param url
     *            the url of a page (generally the first) of the chapter
     */
    Chapter( final String number, final URL url ) {
        this.number = number;
        this.chapterUrl = url;
    }

    /**
     * Getter for this Chapter's number
     * 
     * @return the number of this Chapter. Might be non-numerical, so it is a String.
     */
    public String getNumber() {
        return this.number;
    }

    /**
     * Getter for this Chapter's url
     * 
     * @return the {@link URL} of this Chapter. Most times this is the URL of the first page, but the other pages should work as
     *         well.
     */
    public URL getChapterUrl() {
        return this.chapterUrl;
    }

    @Override
    public String toString() {
        return MessageFormat.format( "{0}: {1}", this.number, this.getChapterUrl() );
    }

}
