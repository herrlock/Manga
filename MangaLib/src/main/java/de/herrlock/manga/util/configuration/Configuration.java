package de.herrlock.manga.util.configuration;

import java.io.File;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.Properties;

import de.herrlock.manga.exceptions.InitializeException;
import de.herrlock.manga.util.ChapterPattern;
import de.herrlock.manga.util.Constants;

public abstract class Configuration {

    protected static URL _createUrl( Properties p ) {
        // get url
        URL url;
        try {
            String urlString = p.getProperty( Constants.PARAM_URL );
            if ( urlString == null || "".equals( urlString ) ) {
                throw new InitializeException( "url is not filled but required" );
            }
            url = new URL( urlString );
        } catch ( MalformedURLException ex ) {
            throw new InitializeException( "url is malformed", ex );
        }
        return url;
    }

    protected static Proxy _createProxy( Properties p ) {
        // get proxy
        try {
            String urlString = p.getProperty( Constants.PARAM_PROXY );
            if ( urlString != null && !"".equals( urlString ) ) {
                URL proxyUrl = new URL( urlString );
                String proxyHost = proxyUrl.getHost();
                int proxyPort = proxyUrl.getPort();
                InetSocketAddress sa = new InetSocketAddress( proxyHost, proxyPort );
                return new Proxy( Proxy.Type.HTTP, sa );
            }
        } catch ( MalformedURLException ex ) {
            throw new InitializeException( "proxy-url is malformed", ex );
        }
        return Proxy.NO_PROXY;
    }

    protected static ChapterPattern _createPattern( Properties p ) {
        String patternString = p.getProperty( Constants.PARAM_PATTERN );
        return new ChapterPattern( patternString );
    }

    protected static int _createTimeout( Properties p ) {
        String timeoutString = p.getProperty( Constants.PARAM_TIMEOUT );
        if ( timeoutString == null || timeoutString.trim().equals( "" ) ) {
            return Constants.PARAM_TIMEOUT_DEFAULT;
        }
        return Integer.parseInt( timeoutString );
    }

    protected static File _createFolderwatch( Properties p ) {
        String fwPath = p.getProperty( Constants.PARAM_JDFW );
        if ( fwPath == null || fwPath.trim().equals( "" ) ) {
            throw new InitializeException( "property " + Constants.PARAM_JDFW + " is required but not availabile." );
        }
        return new File( fwPath );
    }

    protected Configuration() {
        // not used directly
    }
}
