package de.herrlock.manga.util.configuration;

import java.io.File;
import java.net.URL;
import java.util.Objects;
import java.util.Properties;

import org.apache.http.HttpHost;

import de.herrlock.manga.util.ChapterPattern;

/**
 * A {@link Configuration} containing settings needed for a download via the JDowloader-plugin "folderwatch"
 * 
 * @author HerrLock
 */
public class JDConfiguration extends DownloadConfiguration {
    private final File folderwatch;

    public static JDConfiguration create( final Properties p ) {
        boolean headless = _getIsHeadless( p );
        URL url = _createUrl( p );
        HttpHost proxy = _createProxy( p );
        ChapterPattern pattern = _createPattern( p );
        int timeout = _createTimeout( p );
        File folderwatch = _createFolderwatch( p );
        return new JDConfiguration( headless, url, proxy, pattern, timeout, folderwatch );
    }

    /**
     * constructor for a {@link JDConfiguration}
     * 
     * @param headless
     *            if the downloader runs in cli-mode (true) or with a type of gui (false)
     * @param url
     *            an {@link URL} to the manga's base-page.
     * @param proxy
     *            a {@link HttpHost} containing schema, host-name and port of the used proxy. Or {@code null} to use no proxy.
     * @param pattern
     *            the {@link ChapterPattern} to use. Or null to use the default {@link ChapterPattern} to download all.
     * @param timeout
     *            the timeout for the http-requests. The dafult-value is used if negative.
     * @param folderwatch
     *            the {@link File} pointing to the folderwatch-folder from the local JDownloader-installation
     */
    public JDConfiguration( final boolean headless, final URL url, final HttpHost proxy, final ChapterPattern pattern,
        final int timeout, final File folderwatch ) {
        super( headless, url, proxy, pattern, timeout );
        this.folderwatch = Objects.requireNonNull( folderwatch, "The folderwatch-folder must not be null" );
    }

    /**
     * Getter for the folderwatch-folder
     * 
     * @return the {@link File} that points to the folderwatch-folder from the local JJDownloader-installation from this
     *         {@link JDConfiguration}
     */
    public final File getFolderwatch() {
        return this.folderwatch;
    }
}
