package de.herrlock.manga.http;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author HerrLock
 */
public final class ServerMain {
    private static final Logger logger = LogManager.getLogger();

    private final Server server;

    public static void execute() throws ServletException, LifecycleException, IOException, URISyntaxException {
        logger.traceEntry();
        execute( false );
    }

    public static void execute( final boolean withDesktop )
        throws ServletException, LifecycleException, IOException, URISyntaxException {
        logger.traceEntry( "withDesktop: {}", withDesktop );
        ServerMain srvMain = new ServerMain();
        srvMain.start();
        if ( withDesktop && Desktop.isDesktopSupported() ) {
            Desktop.getDesktop().browse( new URI( "http://localhost:1905" ) );
        }
        srvMain.listenForStop();
    }

    public ServerMain() throws ServletException {
        logger.traceEntry();
        this.server = new Server();
    }

    public void start() throws LifecycleException, IOException {
        logger.traceEntry();
        this.server.start();
    }

    public void listenForStop() {
        logger.traceEntry();
        this.server.listenForStop();
    }

    public void stop() throws LifecycleException {
        logger.traceEntry();
        this.server.stop();
    }
}
