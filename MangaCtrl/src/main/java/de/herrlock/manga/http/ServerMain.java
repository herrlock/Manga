package de.herrlock.manga.http;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.exceptions.MDRuntimeException;

/**
 * @author HerrLock
 */
public final class ServerMain {
    private static final Logger logger = LogManager.getLogger();

    private final Server server;

    public static void main( final String... args ) {
        logger.entry();
        ServerMain srvMain = new ServerMain();
        srvMain.startServer();
    }

    public ServerMain() {
        this.server = Server.createDefault();
    }

    public void startServer() {
        logger.entry();
        try {
            this.server.start();
        } catch ( Exception ex ) {
            throw new MDRuntimeException( ex );
        }
    }

    public void stopServer() {
        logger.entry();
        this.server.stop();
    }
}
