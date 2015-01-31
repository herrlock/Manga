package de.herrlock.manga.connection;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

public class DirectConnectionFactory extends ConnectionFactory {

    public DirectConnectionFactory( String timeout ) {
        super( timeout );
    }

    @Override
    protected URLConnection getRawConnection( URL url ) throws IOException {
        return url.openConnection();
    }

}
