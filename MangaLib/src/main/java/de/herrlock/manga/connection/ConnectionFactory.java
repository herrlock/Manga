package de.herrlock.manga.connection;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;

import de.herrlock.manga.util.Constants;

public abstract class ConnectionFactory {

    private final int timeout;

    /**
     * a factory for connections using the given timeout
     * 
     * @param timeout
     *            the timeout for the new connections
     */
    public ConnectionFactory( String timeout ) {
        if ( timeout != null && !"".equals( timeout ) ) {
            this.timeout = Integer.parseInt( timeout );
        } else {
            this.timeout = Constants.PARAM_TIMEOUT_DEFAULT;
        }
    }

    /**
     * creates a connection from {@link #getRawConnection(URL)}, sets the connectTimeout to the current timeout-value and the
     * read-timeout to the double timeout-value
     * 
     * @param url
     *            the URL to connect to
     * @return the connection from the given URL
     * @throws IOException
     *             if an I/O exception occurs.
     */
    public URLConnection getConnection( URL url ) throws IOException {
        URLConnection con = getRawConnection( url );
        con.setConnectTimeout( this.timeout );
        con.setReadTimeout( 2 * this.timeout );
        return con;
    }

    /**
     * creates a connection from the given URL
     * 
     * @param url
     *            the URL to connect to
     * @return a connection to customize further
     * @throws IOException
     */
    protected abstract URLConnection getRawConnection( URL url ) throws IOException;

}
