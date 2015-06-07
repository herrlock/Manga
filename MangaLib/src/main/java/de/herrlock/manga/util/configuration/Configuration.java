package de.herrlock.manga.util.configuration;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Properties;

import de.herrlock.exceptions.InitializeException;
import de.herrlock.manga.util.ChapterPattern;
import de.herrlock.manga.util.Constants;

public abstract class Configuration {

    protected static URL _createUrl( Properties p ) {
        // get url
        URL url;
        try {
            String urlString = p.getProperty( Constants.PARAM_URL );
            url = new URL( urlString );
        } catch ( MalformedURLException ex ) {
            throw new InitializeException( "url is malformed", ex );
        }
        return url;
    }

    protected static Proxy _createProxy( Properties p ) {
        // get proxy
        URL proxyUrl;
        try {
            proxyUrl = new URL( p.getProperty( Constants.PARAM_PROXY ) );
        } catch ( MalformedURLException ex ) {
            throw new InitializeException( "proxy-url is malformed", ex );
        }
        String proxyHost = proxyUrl.getHost();
        int proxyPort = proxyUrl.getPort();
        InetSocketAddress sa = new InetSocketAddress( proxyHost, proxyPort );
        Proxy proxy = new Proxy( Proxy.Type.HTTP, sa );
        return proxy;
    }

    protected static ChapterPattern _createPattern( Properties p ) {
        return new ChapterPattern( p.getProperty( Constants.PARAM_PATTERN ) );
    }

    protected static File _createFolderwatch( Properties p ) {
        String fwPath = p.getProperty( Constants.PARAM_JDFW );
        return new File( fwPath );
    }
}
