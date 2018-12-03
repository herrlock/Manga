package de.herrlock.manga.util.configuration;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Objects;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.client.Origin;
import org.eclipse.jetty.util.B64Code;

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
    public static final int TIMEOUT_DEFAULT = 5_000;
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

    private final boolean isHeadless;

    /**
     * creates a boolean from the property {@value #HEADLESS} in the given Properties
     * 
     * @param p
     *            the {@link Properties} where to search for the headless-property
     * @return the property headless as bollean
     */
    protected static boolean _getIsHeadless( final Properties p ) {
        logger.traceEntry();
        String property = p.getProperty( HEADLESS, "false" );
        boolean headless = property.matches( "true" );
        logger.debug( "headless: {}", headless );
        return headless;
    }

    /**
     * creates a {@link URL} from the property {@value #URL} in the given Properties
     * 
     * @param p
     *            the {@link Properties} where to search for an url
     * @return the created URL
     * @throws InitializeException
     *             in case the URL is not availabile or malformed
     */
    protected static URL _createUrl( final Properties p ) throws InitializeException {
        logger.traceEntry();
        URL url = _createUrlNotRequired( p );
        if ( url == null ) {
            throw new InitializeException( "url is not filled but required" );
        }
        logger.debug( "URL: {}", url );
        return url;
    }

    /**
     * creates a {@link URL} from the property {@value #URL} in the given Properties
     * 
     * @param p
     *            the {@link Properties} where to search for an url
     * @return the created URL
     * @throws InitializeException
     *             in case the URL is not availabile or malformed
     */
    protected static URL _createUrlNotRequired( final Properties p ) throws InitializeException {
        logger.traceEntry();
        // get url
        try {
            String urlString = p.getProperty( URL );
            if ( urlString == null || "".equals( urlString ) ) {
                return null;
            }
            return new URL( urlString );
        } catch ( final MalformedURLException ex ) {
            logger.error( "", ex );
            throw new InitializeException( "url is malformed", ex );
        }
    }

    /**
     * Creates a {@link ProxyStorage} that serves as proxy
     * 
     * @param p
     *            the {@link Properties} where to search for proxy-settings
     * @return a {@link ProxyStorage} containing the proxy-settings. Does not return null, instead the {@link ProxyStorage}
     *         contains {@code null}-values
     * @throws InitializeException
     *             in case the given url is malformed or cannot be resolved
     */
    protected static ProxyStorage _createProxy( final Properties p ) throws InitializeException {
        logger.traceEntry();
        // get proxy
        try {
            String proxyString = p.getProperty( PROXY );
            if ( proxyString != null && !"".equals( proxyString ) ) {
                return createProxyStorage( proxyString );
            }
        } catch ( final MalformedURLException ex ) {
            logger.error( "", ex );
            throw new InitializeException( "proxy-url is malformed or not recognized.", ex );
        }
        logger.debug( "No Proxy" );
        return new ProxyStorage();
    }

    protected static ProxyStorage createProxyStorage( final String urlString ) throws MalformedURLException {
        if ( urlString == null ) {
            return new ProxyStorage();
        }
        final URL url;
        if ( urlString.startsWith( "http://" ) || urlString.startsWith( "https://" ) ) {
            url = new URL( urlString );
        } else {
            url = new URL( "http://" + urlString );
        }
        String proxyHost = url.getHost();
        int proxyPort = url.getPort();
        String scheme = url.getProtocol();
        logger.debug( "Proxy-URL: {}:{}", proxyHost, proxyPort );
        Origin origin = new Origin( scheme, proxyHost, proxyPort );

        ProxyStorage proxyStorage;
        String userInfo = url.getUserInfo();
        if ( userInfo == null ) {
            logger.debug( "No proxy-authentification" );
            proxyStorage = new ProxyStorage( origin );
        } else {
            String[] userInfoSplit = userInfo.split( ":" );
            String proxyUser = userInfoSplit[0];
            String proxyPassword = userInfoSplit[1];
            logger.debug( "Proxy-User: {}", proxyUser );
            proxyStorage = new ProxyStorage( origin, proxyUser, proxyPassword );
        }

        return proxyStorage;
    }

    /**
     * Creates a {@link ChapterPattern} to choose specific Chapters to download
     * 
     * @param p
     *            the {@link Properties} where to search for a pattern
     * @return a {@link ChapterPattern} containing the containing the chosen Chaptern
     */
    protected static ChapterPattern _createPattern( final Properties p ) {
        logger.traceEntry();
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
        logger.traceEntry();
        String timeoutString = p.getProperty( TIMEOUT );
        if ( timeoutString == null || timeoutString.trim().equals( "" ) ) {
            logger.debug( "Timeout: using default-value ({})", TIMEOUT_DEFAULT );
            return TIMEOUT_DEFAULT;
        }
        logger.debug( "Timeout: {}", timeoutString );
        // timeout in seconds
        return Integer.parseInt( timeoutString ) * 1000;
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
        logger.traceEntry();
        String fwPath = p.getProperty( JDFW );
        if ( fwPath == null || fwPath.trim().equals( "" ) ) {
            throw new InitializeException( "property " + JDFW + " is required but not availabile." );
        }
        logger.debug( "Folderwatch-Folder: {}", fwPath );
        return new File( fwPath );
    }

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
    protected Configuration( final boolean isHeadless ) {
        this.isHeadless = isHeadless;
    }

    /**
     * getter for isHeadless
     * 
     * @return isHeadless
     */
    public final boolean isHeadless() {
        return this.isHeadless;
    }

    /**
     * A class that contains the address to a proxy (hostname and port) and (optional) Base64-encoded credentials to authenticate
     * with
     * 
     * @author HerrLock
     */
    public static class ProxyStorage {
        private final Origin origin;
        private final String b64creds;

        public ProxyStorage() {
            this( null );
        }

        /**
         * Creates a ProxyStorage with a proxy but no credentials. This constructor should be used, when no authentication is
         * required.
         * 
         * @param origin
         *            the proxy to authenticate at
         */
        public ProxyStorage( final Origin origin ) {
            this( origin, null );
        }

        /**
         * Creates a new ProxyStorage with the given proxy-url, username and password.
         * 
         * @param origin
         *            the proxy to authenticate at
         * @param user
         *            the username to authenticate with
         * @param password
         *            the password to authenticate with
         */
        public ProxyStorage( final Origin origin, final String user, final String password ) {
            this( origin, B64Code.encode( ( Objects.requireNonNull( user, "user must not be null" ) + ":"
                + Objects.requireNonNull( password, "password must not be null" ) ) ) );
        }

        /**
         * Creates a new ProxyStorage with the given proxy-url, and Base64-credentials.
         * 
         * @param origin
         *            the proxy to authenticate at
         * @param b64creds
         *            the Base64-encoded credentials to authenticate with
         */
        public ProxyStorage( final Origin origin, final String b64creds ) {
            this.origin = origin;
            this.b64creds = b64creds;
        }

        /**
         * Getter for the proxy-host. {@code null} if no proxy exist.
         * 
         * @return an Origin containins the address to the proxy
         */
        public Origin getOrigin() {
            return this.origin;
        }

        /**
         * Getter for the proxy-credentials. {@code null} if no credentials exist.
         * 
         * @return the Base64-encoded credentials
         */
        public String getCreds() {
            return this.b64creds;
        }

        @Override
        public String toString() {
            return MessageFormat.format( "ProxyStorage( host: {0}, auth: {1} )", this.origin, this.b64creds != null );
        }
    }
}
