package de.herrlock.manga.host;

import java.io.IOException;

import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * @author HerrLock
 */
public abstract class InstantiationProxy {
    public abstract ChapterList getInstance( DownloadConfiguration conf ) throws IOException;

    public Class<? extends ChapterList> getProxiedClass() {
        ProxyDetails proxyDetails = getClass().getAnnotation( ProxyDetails.class );
        if ( proxyDetails == null ) {
            throw new IllegalStateException( "No annotation @ProxyDetails given. Consider adding it or override this method." );
        }
        Class<? extends ChapterList> proxiedClass = proxyDetails.proxiedClass();
        if ( proxiedClass == null ) {
            throw new IllegalStateException(
                "The annotation @ProxyDetails does not contain the proxied class. This should not happen." );
        }
        return proxiedClass;
    }
}
