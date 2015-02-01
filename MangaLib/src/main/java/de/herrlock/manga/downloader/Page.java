package de.herrlock.manga.downloader;

import java.io.File;
import java.net.URL;

public class Page {
    private final URL pageUrl;
    private final File chapterFolder;
    private final int pageNumber;

    public Page( URL pageUrl, File chapterFolder, int pageNumber ) {
        this.pageUrl = pageUrl;
        this.chapterFolder = chapterFolder;
        this.pageNumber = pageNumber;
    }

    public URL getURL() {
        return this.pageUrl;
    }

    public File getFolder() {
        return this.chapterFolder;
    }

    public int getNumber() {
        return this.pageNumber;
    }
}
