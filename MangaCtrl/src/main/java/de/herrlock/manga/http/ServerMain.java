package de.herrlock.manga.http;

import java.io.IOException;
import java.net.SocketException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.http.location.BackgroundImageLocation;
import de.herrlock.manga.http.location.IndexHtmlLocation;
import de.herrlock.manga.http.location.JQueryLocation;
import de.herrlock.manga.http.location.StartDownloadLocation;

/**
 * @author HerrLock
 */
public final class ServerMain {
    private static final Logger logger = LogManager.getLogger();

    private final Server server;
    private Thread serverThread;

    public static void main( String... args ) {
        logger.entry();
        ServerMain srvMain = new ServerMain();
        srvMain.startServer();
    }

    public ServerMain() {
        this( 1905 );
    }

    public ServerMain( int port ) {
        try {
            this.server = new Server( port );
            this.addDefaultLocations();
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }

    public void startServer() {
        logger.entry();
        if ( this.serverThread == null ) {
            logger.debug( "serverThread created" );
            this.serverThread = new Thread( this.server );
        }
        if ( !this.serverThread.isAlive() ) {
            logger.debug( "serverThread started" );
            this.serverThread.start();
        }
    }

    public void stopServer() {
        logger.entry();
        try {
            this.server.stopServer();
            this.server.close();
        } catch ( SocketException ex ) {
            this.serverThread.interrupt();
            this.serverThread = null;
            logger.info( "stopped server" );
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }

    private void addDefaultLocations() {
        // "/", default-page
        this.server.registerLocation( new IndexHtmlLocation() );
        // "/start", to start
        this.server.registerLocation( new StartDownloadLocation() );

        // "/jquery.js", returns jquery
        this.server.registerLocation( new JQueryLocation() );
        // "/background.jpg", returns an random entry from a collection of images
        this.server.registerLocation( new BackgroundImageLocation() );
    }

}
