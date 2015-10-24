package de.herrlock.manga.jd;

import java.net.URL;
import java.text.MessageFormat;

/**
 * A class representing a single download (url => filename)
 * 
 * @author HerrLock
 */
final class CrawljobEntry {
    private final String filename;
    private final String url;

    /**
     * creates a CrawljobEntry of the resource with the given filename at the given URL
     * 
     * @param filename
     *            the name to save the downloaded image with
     * @param url
     *            the {@link URL} to download the image from
     */
    public CrawljobEntry( final String filename, final String url ) {
        this.filename = filename;
        this.url = url;
    }

    /**
     * creates a String that represents the current CrawljobEntry
     * 
     * @return string for the current CrawljobEntry-object
     */
    public String export() {
        return MessageFormat.format( "text={0}{1}filename={2}", this.url, System.lineSeparator(), this.filename );
    }
}
