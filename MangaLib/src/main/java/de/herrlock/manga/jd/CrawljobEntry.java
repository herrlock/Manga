package de.herrlock.manga.jd;

import java.net.URL;

/**
 * A class representing a single download (url => filename)
 * 
 * @author HerrLock
 */
class CrawljobEntry {
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
    public CrawljobEntry( String filename, String url ) {
        this.filename = filename;
        this.url = url;
    }

    /**
     * creates a String that represents the current CrawljobEntry
     * 
     * @return string for the current CrawljobEntry-object
     */
    public String export() {
        StringBuilder sb = new StringBuilder();
        sb.append( "text=" );
        sb.append( this.url );
        sb.append( System.lineSeparator() );
        sb.append( "filename=" );
        sb.append( this.filename );
        sb.append( ".jpg" );
        return sb.toString();
    }
}
