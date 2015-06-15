package de.herrlock.manga.http;

import java.io.IOException;

import de.herrlock.manga.http.location.StartDownloadLocation;
import de.herrlock.manga.http.location.BackgroundImageLocation;
import de.herrlock.manga.http.location.IndexHtmlLocation;
import de.herrlock.manga.http.location.JQueryLocation;

/**
 * @author HerrLock
 */
public final class ServerMain {

    public static void main( String... args ) {
        try ( Server server = new Server() ) {
            addLocations( server );
            Thread serverThread = new Thread( server );
            serverThread.start();
            serverThread.join();
        } catch ( IOException | InterruptedException ex ) {
            throw new RuntimeException( ex );
        }
    }

    private static void addLocations( Server server ) {
        // /md, default-page
        server.registerLocation( new IndexHtmlLocation() );
        // /start, to start
        server.registerLocation( new StartDownloadLocation() );

        // /jquery.js, returns jquery
        server.registerLocation( new JQueryLocation() );

        server.registerLocation( new BackgroundImageLocation() );
    }

    private ServerMain() {
        // nothing to do
    }
}
