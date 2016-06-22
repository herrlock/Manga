package de.herrlock.manga.http;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import javax.servlet.ServletException;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.startup.Tomcat;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.annotations.VisibleForTesting;

/**
 * @author HerrLock
 */
public final class Server {
    private static final Logger logger = LogManager.getLogger();
    private static final Path tomcatFolder = Paths.get( ".", "tomcat" );
    private final Tomcat tomcat = new Tomcat();
    private final int port;

    /**
     * Creates a Server, starts it and waits until if receives the signal to stop.
     * 
     * @throws ServletException
     *             thrown be {@link Server#Server()}
     * @throws LifecycleException
     *             thrown be {@link Server#start()}
     * @throws IOException
     *             thrown by {@link Server#start()}
     */
    public static void startServerAndWaitForStop() throws ServletException, LifecycleException, IOException {
        final Server server = new Server();
        server.start();
        server.listenForStop();
    }

    /**
     * Creates a new Embed-Tomcat. The server listens to the port 1905.
     * 
     * @throws ServletException
     *             thrown by {@link Tomcat#addWebapp(String, String)}
     * @throws IOException
     *             thrown by {@link Files#createDirectories(Path, java.nio.file.attribute.FileAttribute...)}
     */
    public Server() throws ServletException, IOException {
        this( 1905 );
    }

    Server( final int port ) throws ServletException, IOException {
        this.tomcat.setBaseDir( tomcatFolder.toString() );
        this.port = port;
        this.tomcat.setPort( port );

        Context serverContext;
        Path docBase = Files.createDirectories( tomcatFolder.resolve( "temp" ) );
        serverContext = this.tomcat.addContext( "/server", docBase.toAbsolutePath().toString() );
        Tomcat.addServlet( serverContext, "StopServer", new StopServerServlet( this ) );
        serverContext.addServletMapping( "/stop", "StopServer" );

        this.tomcat.addWebapp( "", tomcatFolder.resolve( "webapps/ROOT.war" ).toAbsolutePath().toString() );
    }

    /**
     * Start the tomcat-instance
     * 
     * @throws LifecycleException
     *             thrown by {@link Tomcat#start()}
     * @throws IOException
     *             thrown if the tomcat's connector is in the state {@link LifecycleState#FAILED} after starting.
     */
    public void start() throws LifecycleException, IOException {
        this.tomcat.start();

        logger.debug( "Server: {} ({})", this.tomcat.getServer(), this.tomcat.getServer().getClass() );
        logger.debug( "Service: {} ({})", this.tomcat.getService(), this.tomcat.getService().getClass() );
        logger.debug( "Engine: {} ({})", this.tomcat.getEngine(), this.tomcat.getEngine().getClass() );
        logger.debug( "Host: {} ({})", this.tomcat.getHost(), this.tomcat.getHost().getClass() );
        logger.debug( "Connector: {} ({})", this.tomcat.getConnector(), this.tomcat.getConnector().getClass() );

        if ( this.tomcat.getConnector().getState() == LifecycleState.FAILED ) {
            IOException ioException = new IOException( "Something is already running on Port " + this.port );
            logger.error( ioException.getMessage() );
            throw ioException;
        }
        logger.info( "Server started: http://localhost:{}", this.tomcat.getConnector().getPort() );
    }

    /**
     * Wait for the server to be stopped. CHecks all 2 seconds if either 'q' can be read from System.in or the connector is
     * stopped (this can be done by the method {@link Tomcat#stop()}.
     */
    public void listenForStop() {
        boolean active = true;
        boolean sysinIsOpen = true;
        try {
            System.in.available();
            logger.info( "Enter 'q' to quit" );
        } catch ( IOException ex ) {
            logger.debug( "System.in threw Exception: ", ex );
            sysinIsOpen = false;
        }
        while ( active ) {
            logger.debug( "Server active" );
            boolean connectorStopped = getConnectorStopped();
            boolean quitBySysin = sysinIsOpen && getStopFromSysin();
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

    @VisibleForTesting
    boolean getConnectorStopped() {
        logger.traceEntry();
        LifecycleState state = this.tomcat.getConnector().getState();
        boolean connectorStopped = state == LifecycleState.STOPPED;
        logger.debug( "Connector: {}", state );
        return connectorStopped;
    }

    private boolean getStopFromSysin() {
        logger.traceEntry();
        boolean quitBySysin = false;
        try {
            if ( System.in.available() > 0 ) {
                int read = System.in.read();
                quitBySysin = read == 'q';
                logger.info( "Read char: {}", ( char ) read );
            }
        } catch ( IOException ex ) {
            // System.in is closed, this might happen when called from javaw
            logger.info( ex );
        }
        return quitBySysin;
    }

    /**
     * Delegate for {@link Tomcat#stop()}.
     * 
     * @throws LifecycleException
     *             thrown by {@link Tomcat#stop()}
     */
    public void stop() throws LifecycleException {
        this.tomcat.stop();
    }

}
