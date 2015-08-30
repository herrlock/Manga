package de.herrlock.manga.http;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author HerrLock
 */
public final class StartWithDesktop {
    public static void main( String... args ) throws IOException, URISyntaxException {
        ServerMain.main( args );
        if ( Desktop.isDesktopSupported() ) {
            Desktop.getDesktop().browse( new URI( "http://localhost:1905" ) );
        }
    }

    private StartWithDesktop() {
        // not used
    }
}
