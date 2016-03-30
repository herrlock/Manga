package de.herrlock.manga.http;

import java.io.File;
import java.io.IOException;

import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author HerrLock
 */
public class Server {
    private static final Logger logger = LogManager.getLogger();
    private final Tomcat tomcat = new Tomcat();

    public static void startServerAndWaitForStop() throws ServletException, LifecycleException, IOException {
        final Server server = new Server();
        server.start();
        server.listenForStop();
    }

    public Server() throws ServletException {
        this.tomcat.setPort( 1905 );

        Context serverContext = this.tomcat.addContext( "server", new File( ".", "tomcat.1905/temp" ).getAbsolutePath() );
        Tomcat.addServlet( serverContext, "StopServer", new StopServerServlet( this ) );
        serverContext.addServletMapping( "/stop", "StopServer" );

        this.tomcat.addWebapp( "", new File( ".", "tomcat.1905/webapps/ROOT.war" ).getAbsolutePath() );
    }

    public void start() throws LifecycleException, IOException {
        this.tomcat.start();

        logger.info( "Server: {} ({})", this.tomcat.getServer(), this.tomcat.getServer().getClass() );
        logger.info( "Service: {} ({})", this.tomcat.getService(), this.tomcat.getService().getClass() );
        logger.info( "Engine: {} ({})", this.tomcat.getEngine(), this.tomcat.getEngine().getClass() );
        logger.info( "Host: {} ({})", this.tomcat.getHost(), this.tomcat.getHost().getClass() );
        logger.info( "Connector: {} ({})", this.tomcat.getConnector(), this.tomcat.getConnector().getClass() );

        if ( this.tomcat.getConnector().getState() == LifecycleState.FAILED ) {
            IOException ioException = new IOException( "Something is already running on Port 1905" );
            logger.error( ioException.getMessage() );
            throw ioException;
        }
    }

    public void listenForStop() throws IOException {
        boolean active = true;
        while ( active ) {
            LifecycleState state = this.tomcat.getConnector().getState();
            boolean connectorStopped = state == LifecycleState.STOPPED;
            logger.debug( "Serverstatus: {}", state );
            boolean quitBySysin = false;
            if ( System.in.available() > 0 ) {
                int read = System.in.read();
                quitBySysin = read == 'q';
                logger.info( "Read char: {}", ( char ) read );
            }
            if ( connectorStopped || quitBySysin ) {
                active = false;
            } else {
                try {
                    Thread.sleep( 2000 );
                } catch ( InterruptedException ex ) {
                    logger.error( ex );
                }
            }
        }
        logger.info( "Server stopped" );
    }

    public void stop() throws LifecycleException {
        this.tomcat.stop();
    }

}
