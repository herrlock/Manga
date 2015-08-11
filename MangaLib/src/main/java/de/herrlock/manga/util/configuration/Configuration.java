package de.herrlock.manga.util.configuration;

import java.io.File;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.Properties;

import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.exceptions.InitializeException;
import de.herrlock.manga.util.ChapterPattern;
import de.herrlock.manga.util.Constants;

public abstract class Configuration {
    protected static final Logger logger = LogManager.getLogger();

    protected static URL _createUrl( Properties p ) {
        logger.entry();
        // get url
        URL url;
        try {
            String urlString = p.getProperty( Constants.PARAM_URL );
            if ( urlString == null || "".equals( urlString ) ) {
                throw new InitializeException( "url is not filled but required" );
            }
            url = new URL( urlString );
        } catch ( MalformedURLException ex ) {
            logger.error( "", ex );
            throw new InitializeException( "url is malformed", ex );
        }
        logger.debug( "URL: {}", url );
        return url;
    }

    protected static HttpHost _createProxy( Properties p ) {
        logger.entry();
        // get proxy
        try {
            String urlString = p.getProperty( Constants.PARAM_PROXY );
            if ( urlString != null && !"".equals( urlString ) ) {
                if ( !urlString.startsWith( "http://" ) ) {
                    urlString = "http://" + urlString;
                }
                URL proxyUrl = new URL( urlString );
                String proxyHost = proxyUrl.getHost();
                int proxyPort = proxyUrl.getPort();
                InetAddress proxyAddress = InetAddress.getByName( proxyHost );
                logger.debug( "Proxy: {}:{}", proxyHost, proxyPort );
                return new HttpHost( proxyAddress, proxyPort );
            }
        } catch ( MalformedURLException | UnknownHostException ex ) {
            logger.error( "", ex );
            throw new InitializeException( "proxy-url is malformed or not recognized.", ex );
        }
        logger.debug( "No Proxy" );
        return null;
    }

    protected static ChapterPattern _createPattern( Properties p ) {
        logger.entry();
        String patternString = p.getProperty( Constants.PARAM_PATTERN );
        logger.debug( "ChapterPattern: {}", patternString );
        return new ChapterPattern( patternString );
    }

    protected static int _createTimeout( Properties p ) {
        logger.entry();
        String timeoutString = p.getProperty( Constants.PARAM_TIMEOUT );
        if ( timeoutString == null || timeoutString.trim().equals( "" ) ) {
            logger.debug( "Timeout: Default ({})", Constants.PARAM_TIMEOUT_DEFAULT );
            return Constants.PARAM_TIMEOUT_DEFAULT;
        }
        logger.debug( "Timeout: {}", timeoutString );
        return Integer.parseInt( timeoutString );
    }

    protected static File _createFolderwatch( Properties p ) {
        logger.entry();
        String fwPath = p.getProperty( Constants.PARAM_JDFW );
        if ( fwPath == null || fwPath.trim().equals( "" ) ) {
            throw new InitializeException( "property " + Constants.PARAM_JDFW + " is required but not availabile." );
        }
        logger.debug( "Folderwatch-Folder: {}", fwPath );
        return new File( fwPath );
    }

    protected Configuration() {
        // not used directly
    }
}
