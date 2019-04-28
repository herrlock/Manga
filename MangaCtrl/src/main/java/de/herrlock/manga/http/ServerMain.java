package de.herrlock.manga.http;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.exceptions.MDException;
import de.herrlock.manga.http.server.JettyServer;

/**
 * @author HerrLock
 */
public final class ServerMain {
    private static final Logger logger = LogManager.getLogger();

    private final JettyServer server;

    public static void execute() throws IOException, URISyntaxException, MDException {
        logger.traceEntry();
        execute( false );
    }

    public static void execute( final boolean withDesktop ) throws IOException, URISyntaxException, MDException {
        logger.traceEntry( "withDesktop: {}", withDesktop );
        ServerMain srvMain = new ServerMain();
        srvMain.start();
        if ( withDesktop && Desktop.isDesktopSupported() ) {
            Desktop.getDesktop().browse( new URI( "http://localhost:1905" ) );
        }
        srvMain.listenForStop();
        srvMain.stop();
    }

    public ServerMain() {
        logger.traceEntry();
        this.server = new JettyServer();
    }

    public void start() throws MDException {
        logger.traceEntry();
        this.server.start();
    }

    public void listenForStop() {
        logger.traceEntry();
        this.server.listenForStop();
    }

    public void stop() throws MDException {
        logger.traceEntry();
        this.server.stop();
    }
}
