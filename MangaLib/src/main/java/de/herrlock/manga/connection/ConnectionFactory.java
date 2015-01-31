package de.herrlock.manga.connection;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import de.herrlock.manga.util.Constants;

public abstract class ConnectionFactory {

    private final int timeout;

    public URLConnection getConnection( URL url ) throws IOException {
        URLConnection con = getRawConnection( url );
        con.setConnectTimeout( this.timeout );
        con.setReadTimeout( this.timeout );
        return con;
    }

    protected abstract URLConnection getRawConnection( URL url ) throws IOException;

    public ConnectionFactory( String timeout ) {
        if ( timeout != null && !"".equals( timeout ) ) {
            this.timeout = Integer.parseInt( timeout );
        } else {
            this.timeout = Constants.PARAM_TIMEOUT_DEFAULT;
        }
    }

}
