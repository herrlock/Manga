package de.herrlock.manga.util.configuration;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Properties;

import de.herrlock.manga.util.ChapterPattern;

/**
 * A {@link Configuration} containing settings needed for http-connections
 * 
 * @author HerrLock
 */
public class DownloadConfiguration extends Configuration {
    private final URL url;
    private final ChapterPattern pattern;
    private final int timeout;

    /**
     * Creates a {@link DownloadConfiguration} from the given {@link Properties}
     * 
     * @param p
     *            the {@link Properties} where to get the values from
     * @return a new {@link DownloadConfiguration}
     */
    public static DownloadConfiguration create( final Properties p ) {
        boolean isHeadless = _getIsHeadless( p );
        URL url = _createUrl( p );
        ChapterPattern pattern = _createPattern( p );
        int timeout = _createTimeout( p );
        return new DownloadConfiguration( isHeadless, url, pattern, timeout );
    }

    /**
     * Constructor for a {@link DownloadConfiguration}. If the given URL is {@code null} an exception is thrown.
     * 
     * @param isHeadless
     *            if the downloader runs in cli-mode (true) or with a type of gui (false)
     * @param url
     *            an {@link URL} to the manga's base-page.
     * @param pattern
     *            the {@link ChapterPattern} to use. Or null to use the default {@link ChapterPattern} to download all.
     * @param timeout
     *            the timeout for the http-requests. The dafult-value is used if negative.
     */
    public DownloadConfiguration( final boolean isHeadless, final URL url, final ChapterPattern pattern, final int timeout ) {
        this( isHeadless, true, url, pattern, timeout );
    }

    /**
     * Constructor for a {@link DownloadConfiguration}
     * 
     * @param isHeadless
     *            if the downloader runs in cli-mode (true) or with a type of gui (false)
     * @param requireURL
     *            indicates if a URL is required. An exception is thrown, if this parameter is true and the parameter url is
     *            {@code null}.
     * @param url
     *            an {@link URL} to the manga's base-page.
     * @param pattern
     *            the {@link ChapterPattern} to use. Or null to use the default {@link ChapterPattern} to download all.
     * @param timeout
     *            the timeout for the http-requests. The dafult-value is used if negative.
     */
    protected DownloadConfiguration( final boolean isHeadless, final boolean requireURL, final URL url,
        final ChapterPattern pattern, final int timeout ) {
        super( isHeadless );
        this.url = requireURL ? Objects.requireNonNull( url, "A URL is required." ) : url;
        this.pattern = pattern == null ? new ChapterPattern( "" ) : pattern;
        this.timeout = timeout >= 0 ? timeout : TIMEOUT_DEFAULT;
    }

    /**
     * Getter for the URL
     * 
     * @return the {@link URL} from this {@link DownloadConfiguration}
     */
    public final URL getUrl() {
        return this.url;
    }

    /**
     * Getter for the used pattern
     * 
     * @return the {@link ChapterPattern} from this {@link DownloadConfiguration}
     */
    public final ChapterPattern getPattern() {
        return this.pattern;
    }

    /**
     * Getter for the timeout
     * 
     * @return the timeout from this {@link DownloadConfiguration}
     */
    public int getTimeout() {
        return this.timeout;
    }

    @Override
    public String toString() {
        return MessageFormat.format( "URL: {0}, Pattern: {1}, Timeout: {2}", this.url, this.pattern, this.timeout );
    }
}
