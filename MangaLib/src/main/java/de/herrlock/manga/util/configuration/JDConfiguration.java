package de.herrlock.manga.util.configuration;

import java.io.File;
import java.net.Proxy;
import java.net.URL;
import java.util.Properties;

import de.herrlock.manga.util.ChapterPattern;

public class JDConfiguration extends DownloadConfiguration {
    private final File folderwatch;

    public static JDConfiguration create( Properties p ) {
        URL url = _createUrl( p );
        Proxy proxy = _createProxy( p );
        ChapterPattern pattern = _createPattern( p );
        File folderwatch = _createFolderwatch( p );
        return new JDConfiguration( url, proxy, pattern, folderwatch );
    }

    public JDConfiguration( URL url, Proxy proxy, ChapterPattern pattern, File folderwatch ) {
        super( url, proxy, pattern );
        this.folderwatch = folderwatch;
    }

    public final File getFolderwatch() {
        return this.folderwatch;
    }
}
