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

/**
 * A configuration to pass multiple settings in one object around. Can be subclasses to define custom behavior.
 * 
 * @author HerrLock
 */
public abstract class Configuration {
    private static final Logger logger = LogManager.getLogger();

    /**
     * the name of the property for the url
     */
    public static final String URL = "url";
    /**
     * the name of the property for the downloadpattern
     */
    public static final String PATTERN = "pattern";
    /**
     * the name of the property to set a custom timeout with
     */
    public static final String TIMEOUT = "timeout";
    /**
     * the default value of the timeout
     */
    public static final short TIMEOUT_DEFAULT = 5_000;
    /**
     * the path to JDownloader's folderwatch-folder
     */
    public static final String JDFW = "jdfw";
    /**
     * the name of the property for setting the proxy-url
     */
    public static final String PROXY = "proxy";
    /**
     * the name of the property for the setting "headless"
     */
    public static final String HEADLESS = "headless";

    protected static boolean _getIsHeadless( final Properties p ) {
        logger.entry();
        String property = p.getProperty( HEADLESS );
        boolean headless = property.matches( "" );
        logger.debug( "headless: {}", headless );
        return headless;
    }

    /**
     * creates a {@link URL} from the property {@value #URL} in the given Properties
     * 
     * @param p
     *            the {@link Properties} where to search for an url
     * @return the reated URL
     * @throws InitializeException
     *             in case the URL is not availabile or malformed
     */
    protected static URL _createUrl( final Properties p ) throws InitializeException {
        logger.entry();
        // get url
        URL url;
        try {
            String urlString = p.getProperty( URL );
            if ( urlString == null || "".equals( urlString ) ) {
                throw new InitializeException( "url is not filled but required" );
            }
            url = new URL( urlString );
        } catch ( final MalformedURLException ex ) {
            logger.error( "", ex );
            throw new InitializeException( "url is malformed", ex );
        }
        logger.debug( "URL: {}", url );
        return url;
    }

    /**
     * Creates a {@link HttpHost} that serves as proxy
     * 
     * @param p
     *            the {@link Properties} where to search for proxy-settings
     * @return a {@link HttpHost} containing the proxy-settings, or {@code null}, when no proxy is defined
     * @throws InitializeException
     *             in case the given url is malformed or cannot be resolved
     */
    protected static HttpHost _createProxy( final Properties p ) throws InitializeException {
        logger.entry();
        // get proxy
        try {
            String urlString = p.getProperty( PROXY );
            if ( urlString != null && !"".equals( urlString ) ) {
                final URL proxyUrl;
                if ( urlString.startsWith( "http://" ) || urlString.startsWith( "https://" ) ) {
                    proxyUrl = new URL( urlString );
                } else {
                    proxyUrl = new URL( "http://" + urlString );
                }
                String proxyHost = proxyUrl.getHost();
                int proxyPort = proxyUrl.getPort();
                String scheme = proxyUrl.getProtocol();
                InetAddress proxyAddress = InetAddress.getByName( proxyHost );
                logger.debug( "Proxy: {}:{}", proxyHost, proxyPort );
                return new HttpHost( proxyAddress, proxyPort, scheme );
            }
        } catch ( final MalformedURLException | UnknownHostException ex ) {
            logger.error( "", ex );
            throw new InitializeException( "proxy-url is malformed or not recognized.", ex );
        }
        logger.debug( "No Proxy" );
        return null;
    }

    /**
     * Creates a {@link ChapterPattern} to choose specific Chapters to download
     * 
     * @param p
     *            the {@link Properties} where to search for a pattern
     * @return a {@link ChapterPattern} containing the containing the chosen Chaptern
     */
    protected static ChapterPattern _createPattern( final Properties p ) {
        logger.entry();
        String patternString = p.getProperty( PATTERN );
        logger.debug( "ChapterPattern: {}", patternString );
        return new ChapterPattern( patternString );
    }

    /**
     * Parses the timeout from the {@link Properties} or chooses the default-value
     * 
     * @param p
     *            the {@link Properties} where to search for a timeout
     * @return an {@code int} containing the timeout, either the parsed value from the {@link Properties} or the default-value
     *         from {@link #TIMEOUT_DEFAULT} ({@value #TIMEOUT_DEFAULT})
     */
    protected static int _createTimeout( final Properties p ) {
        logger.entry();
        String timeoutString = p.getProperty( TIMEOUT );
        if ( timeoutString == null || timeoutString.trim().equals( "" ) ) {
            logger.debug( "Timeout: using default-value ({})", TIMEOUT_DEFAULT );
            return TIMEOUT_DEFAULT;
        }
        logger.debug( "Timeout: {}", timeoutString );
        return Integer.parseInt( timeoutString );
        // TODO: maybe catch NumberFormatException?
    }

    /**
     * Creates a {@link File} pointing to JDownloader's folderwatch-folder
     * 
     * @param p
     *            the {@link Properties} where to search for a file-description
     * @return a {@link File} pointing to the folderwatch-folder of the local JDownloader-installation
     * @throws InitializeException
     *             in case the required property cannot be found
     */
    protected static File _createFolderwatch( final Properties p ) throws InitializeException {
        logger.entry();
        String fwPath = p.getProperty( JDFW );
        if ( fwPath == null || fwPath.trim().equals( "" ) ) {
            throw new InitializeException( "property " + JDFW + " is required but not availabile." );
        }
        logger.debug( "Folderwatch-Folder: {}", fwPath );
        return new File( fwPath );
    }

    private final boolean isHeadless;

    @Override
    public abstract String toString();

    /**
     * Default-constuctor for subclasses. Calls the constructor {@link Configuration#Configuration(boolean)} with the value
     * {@code false}
     */
    protected Configuration() {
        this( false );
    }

    /**
     * Constructur with the headless-property
     * 
     * @param isHeadless
     *            if the downloader runs in cli-mode (false) or with a type of gui (true)
     */
    protected Configuration( boolean isHeadless ) {
        this.isHeadless = isHeadless;
    }

    /**
     * getter for isHeadless
     * 
     * @return
     */
    public final boolean isHeadless() {
        return this.isHeadless;
    }
}
