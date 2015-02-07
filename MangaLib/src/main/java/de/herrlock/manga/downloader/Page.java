package de.herrlock.manga.downloader;

import java.io.File;
import java.net.URL;

class Page {
    private final URL pageUrl;
    private final File targetFile;

    public Page( URL pageUrl, File chapterFolder, int pageNumber ) {
        this.pageUrl = pageUrl;
        this.targetFile = new File( chapterFolder, pageNumber + ".jpg" );
    }

    public URL getURL() {
        return this.pageUrl;
    }

    public File getTargetFile() {
        return this.targetFile;
    }
}
