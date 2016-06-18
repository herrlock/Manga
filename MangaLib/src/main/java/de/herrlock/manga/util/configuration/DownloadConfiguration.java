package de.herrlock.manga.util.configuration;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Properties;

import org.apache.http.HttpHost;

import de.herrlock.manga.util.ChapterPattern;

/**
 * A {@link Configuration} containing settings needed for http-connections
 * 
 * @author HerrLock
 */
public class DownloadConfiguration extends Configuration {
    private final URL url;
    private final ProxyStorage proxy;
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
        ProxyStorage proxy = _createProxy( p );
        ChapterPattern pattern = _createPattern( p );
        int timeout = _createTimeout( p );
        return new DownloadConfiguration( isHeadless, url, proxy, pattern, timeout );
    }

    /**
     * constructor for a {@link DownloadConfiguration}
     * 
     * @param isHeadless
     *            if the downloader runs in cli-mode (true) or with a type of gui (false)
     * @param url
     *            an {@link URL} to the manga's base-page.
     * @param proxy
     *            a ProxyStorage containing address and auth of the used proxy. Must no be null.
     * @param pattern
     *            the {@link ChapterPattern} to use. Or null to use the default {@link ChapterPattern} to download all.
     * @param timeout
     *            the timeout for the http-requests. The dafult-value is used if negative.
     */
    public DownloadConfiguration( final boolean isHeadless, final URL url, final ProxyStorage proxy, final ChapterPattern pattern,
        final int timeout ) {
        super( isHeadless );
        this.url = Objects.requireNonNull( url, "A URL is required for downloading" );
        this.proxy = proxy == null ? new ProxyStorage() : proxy;
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
     * Getter for the Proxy
     * 
     * @return a {@link HttpHost} containing the proxy-address from this {@link DownloadConfiguration}
     */
    public final ProxyStorage getProxy() {
        return this.proxy;
    }

    /**
     * Getter for the use pattern
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
        return MessageFormat.format( "URL: {0}, Pattern: {1}, Proxy: {2}, Timeout: {3}", this.url, this.pattern, this.proxy,
            this.timeout );
    }
}
