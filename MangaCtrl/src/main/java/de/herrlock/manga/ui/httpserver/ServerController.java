package de.herrlock.manga.ui.httpserver;

import java.util.ResourceBundle;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.http.ServerMain;

public class ServerController {

    private static final Logger logger = LogManager.getLogger();
    public static final ResourceBundle i18n = ResourceBundle.getBundle( "de.herrlock.manga.ui.httpserver.httpserver" );

    private final ServerMain serverCtrl = new ServerMain();

    public void startServer() {
        logger.entry();
        this.serverCtrl.startServer();
    }

    public void stopServer() {
        logger.entry();
        this.serverCtrl.stopServer();
    }

}
