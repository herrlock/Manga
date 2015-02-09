package de.herrlock.manga.connection;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;

public class ProxyConnectionFactory extends ConnectionFactory {

    private Proxy proxy;

    /**
     * a {@linkplain ConnectionFactory} that creates {@linkplain URLConnection Connections} with a proxy
     * 
     * @param timeout
     *            the timeout used for connecting and reading from a URL
     * @param host
     *            the proxy's hostname
     * @param port
     *            the proxy's port
     * @see InetSocketAddress#InetSocketAddress(String, int)
     * @see Proxy#Proxy(java.net.Proxy.Type, java.net.SocketAddress)
     */
    public ProxyConnectionFactory( String timeout, String host, String port ) {
        super( timeout );
        this.proxy = new Proxy( Proxy.Type.HTTP, new InetSocketAddress( host, Integer.parseInt( port ) ) );
    }

    @Override
    protected URLConnection getRawConnection( URL url ) throws IOException {
        return url.openConnection( this.proxy );
    }

}
