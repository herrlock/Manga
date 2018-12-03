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
        int timeout = _createTimeout( p );
        return new IndexerConfiguration( url, timeout );
    }

    /**
     * constructor for an {@link IndexerConfiguration}
     * 
     * @param url
     *            an {@link URL} to the manga's base-page.
     * @param timeout
     *            the timeout for the http-requests. The dafult-value is used if negative.
     */
    public IndexerConfiguration( final URL url, final int timeout ) {
        super( true, false, url, null, timeout );
    }

    @Override
    public String toString() {
        return MessageFormat.format( "URL: {0}, Timeout: {1}", getUrl(), getTimeout() );
    }
}
