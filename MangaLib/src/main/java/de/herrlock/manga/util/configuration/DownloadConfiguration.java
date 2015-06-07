package de.herrlock.manga.util.configuration;

import java.net.Proxy;
import java.net.URL;
import java.util.Properties;

import de.herrlock.manga.util.ChapterPattern;

public class DownloadConfiguration extends Configuration {
    private final URL url;
    private final Proxy proxy;
    private final ChapterPattern pattern;

    public static DownloadConfiguration create( Properties p ) {
        URL url = _createUrl( p );
        Proxy proxy = _createProxy( p );
        ChapterPattern pattern = _createPattern( p );
        return new DownloadConfiguration( url, proxy, pattern );
    }

    public DownloadConfiguration( URL url, Proxy proxy, ChapterPattern pattern ) {
        this.url = url;
        this.proxy = proxy;
        this.pattern = pattern;
    }

    public final URL getUrl() {
        return this.url;
    }

    public final Proxy getProxy() {
        return this.proxy;
    }

    public final ChapterPattern getPattern() {
        return this.pattern;
    }
}
