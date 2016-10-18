package de.herrlock.manga.util.configuration;

import java.net.URL;
import java.text.MessageFormat;
import java.util.Properties;

/**
 * A {@link Configuration} containing settings needed for http-connections
 * 
 * @author HerrLock
 */
public class IndexerConfiguration extends DownloadConfiguration {

    /**
     * Creates an {@link IndexerConfiguration} from the given {@link Properties}
     * 
     * @param p
     *            the {@link Properties} where to get the values from
     * @return a new {@link DownloadConfiguration}
     */
    public static IndexerConfiguration create( final Properties p ) {
        URL url = _createUrlNotRequired( p );
        ProxyStorage proxy = _createProxy( p );
        int timeout = _createTimeout( p );
        return new IndexerConfiguration( url, proxy, timeout );
    }

    /**
     * constructor for an {@link IndexerConfiguration}
     * 
     * @param url
     *            an {@link URL} to the manga's base-page.
     * @param proxy
     *            a ProxyStorage containing address and auth of the used proxy. Must no be null.
     * @param timeout
     *            the timeout for the http-requests. The dafult-value is used if negative.
     */
    public IndexerConfiguration( final URL url, final ProxyStorage proxy, final int timeout ) {
        super( true, false, url, proxy, null, timeout );
    }

    @Override
    public String toString() {
        return MessageFormat.format( "URL: {0}, Proxy: {1}, Timeout: {2}", getUrl(), getProxy(), getTimeout() );
    }
}
