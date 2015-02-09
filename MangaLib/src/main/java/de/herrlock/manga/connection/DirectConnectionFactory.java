package de.herrlock.manga.connection;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class DirectConnectionFactory extends ConnectionFactory {

    /**
     * a {@linkplain ConnectionFactory} that creates {@linkplain URLConnection Connections} without a proxy
     * 
     * @param timeout
     *            the timeout used for connecting and reading from a URL
     */
    public DirectConnectionFactory( String timeout ) {
        super( timeout );
    }

    @Override
    protected URLConnection getRawConnection( URL url ) throws IOException {
        return url.openConnection();
    }

}
