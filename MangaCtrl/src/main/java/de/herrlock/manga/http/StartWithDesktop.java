package de.herrlock.manga.http;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;

/**
 * @author HerrLock
 */
public final class StartWithDesktop {
    public static void main( final String... args ) throws IOException, URISyntaxException, ServletException, LifecycleException {
        ServerMain srvMain = new ServerMain();
        srvMain.start();
        if ( Desktop.isDesktopSupported() ) {
            Desktop.getDesktop().browse( new URI( "http://localhost:1905" ) );
        }
        srvMain.listenForStop();
    }

    private StartWithDesktop() {
        // not used
    }
}
