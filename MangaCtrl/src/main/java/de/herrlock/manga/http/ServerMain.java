package de.herrlock.manga.http;

import java.io.IOException;

import de.herrlock.manga.http.location.AddLocation;
import de.herrlock.manga.http.location.BackgroundImageLocation;
import de.herrlock.manga.http.location.DefaultLocation;
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
        server.registerLocation( new DefaultLocation() );
        // /add, to start
        server.registerLocation( new AddLocation() );

        // /jquery.js, returns jquery
        server.registerLocation( new JQueryLocation() );

        server.registerLocation( new BackgroundImageLocation() );
    }

    private ServerMain() {
        // nothing to do
    }
}
