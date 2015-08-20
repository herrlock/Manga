package de.herrlock.manga.http;

import java.io.IOException;

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
        Thread serverThread = new Thread( this.server );
        serverThread.start();
    }

    public void stopServer() {
        this.server.stopServer();
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
