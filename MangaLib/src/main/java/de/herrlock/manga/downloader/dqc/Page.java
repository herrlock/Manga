package de.herrlock.manga.downloader.dqc;

import java.io.File;
import java.net.URL;

final class Page {
    /**
     * the {@link URL} where to read this page from
     */
    private final URL pageUrl;
    /**
     * the {@link File} where this page will be saved
     */
    private final File targetFile;

    /**
     * creates a new page
     * 
     * @param pageUrl
     *            the {@link URL} where to read from
     * @param chapterFolder
     *            the folder where the page will be stored
     * @param pageNumber
     *            the number of the page, sets the filename (with optional prepended '0')
     */
    public Page( URL pageUrl, File chapterFolder, int pageNumber ) {
        this.pageUrl = pageUrl;
        String _nr = ( pageNumber > 9 ? "" : "0" ) + pageNumber;
        this.targetFile = new File( chapterFolder, _nr + ".jpg" );
    }

    /**
     * getter for this page's {@link URL}
     * 
     * @return the {@link URL} of this page
     */
    public URL getUrl() {
        return this.pageUrl;
    }

    /**
     * getter for this page's save-location
     * 
     * @return the {@link File} where to store this page
     */
    public File getTargetFile() {
        return this.targetFile;
    }
}
