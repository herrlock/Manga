package de.herrlock.manga.util.configuration;

import java.io.File;
import java.net.URL;
import java.util.Properties;

import org.apache.http.HttpHost;

import de.herrlock.manga.util.ChapterPattern;

public class JDConfiguration extends DownloadConfiguration {
    private final File folderwatch;

    public static JDConfiguration create( Properties p ) {
        URL url = _createUrl( p );
        HttpHost proxy = _createProxy( p );
        ChapterPattern pattern = _createPattern( p );
        int timeout = _createTimeout( p );
        File folderwatch = _createFolderwatch( p );
        return new JDConfiguration( url, proxy, pattern, timeout, folderwatch );
    }

    public JDConfiguration( URL url, HttpHost proxy, ChapterPattern pattern, int timeout, File folderwatch ) {
        super( url, proxy, pattern, timeout );
        this.folderwatch = folderwatch;
    }

    public final File getFolderwatch() {
        return this.folderwatch;
    }
}
