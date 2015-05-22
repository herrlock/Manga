package de.herrlock.manga.http;

import java.io.IOException;

/**
 * @author HerrLock
 */
public final class ServerMain {

    public static void main( String... args ) {
        try ( Server server = new Server() ) {
            Thread serverThread = new Thread( server );
            serverThread.start();

            server.registerLocation( new DefaultLocation() );
            server.registerLocation( new AddLocation() );
            server.registerLocation( new JQueryLocation() );

            serverThread.join();
        } catch ( IOException | InterruptedException ex ) {
            throw new RuntimeException( ex );
        }
    }

    private ServerMain() {
        // nothing to do
    }
}
