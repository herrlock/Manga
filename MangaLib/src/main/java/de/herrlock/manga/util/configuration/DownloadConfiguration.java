package de.herrlock.manga.util.configuration;

import java.net.URL;
import java.util.Properties;

import org.apache.http.HttpHost;

import de.herrlock.manga.util.ChapterPattern;

public class DownloadConfiguration extends Configuration {
    private final URL url;
    private final HttpHost proxy;
    private final ChapterPattern pattern;
    private final int timeout;

    public static DownloadConfiguration create( Properties p ) {
        URL url = _createUrl( p );
        HttpHost proxy = _createProxy( p );
        ChapterPattern pattern = _createPattern( p );
        int timeout = _createTimeout( p );
        return new DownloadConfiguration( url, proxy, pattern, timeout );
    }

    public DownloadConfiguration( URL url, HttpHost proxy, ChapterPattern pattern, int timeout ) {
        this.url = url;
        this.proxy = proxy;
        this.pattern = pattern;
        this.timeout = timeout;
    }

    public final URL getUrl() {
        return this.url;
    }

    public final HttpHost getProxy() {
        return this.proxy;
    }

    public final ChapterPattern getPattern() {
        return this.pattern;
    }

    public int getTimeout() {
        return this.timeout;
    }
}
