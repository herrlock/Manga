package de.herrlock.manga.http.jetty;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.security.ConstraintMapping;
import org.eclipse.jetty.security.ConstraintSecurityHandler;
import org.eclipse.jetty.security.HashLoginService;
import org.eclipse.jetty.security.authentication.BasicAuthenticator;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.util.log.Log;
import org.eclipse.jetty.util.security.Constraint;

import de.herrlock.manga.exceptions.MDException;
import de.herrlock.manga.http.jetty.handlers.MangaBaseHandler;
import de.herrlock.manga.http.jetty.log.Log4j2Bridge;

public final class JettyServer {
    private static final Logger logger = LogManager.getLogger();

    /**
     * This must be kept a secret !!!1!!11
     */
    private static final String SUPER_SECRET_MAGIC_TOKEN_FOR_SHUTDOWN = "avadakedavra";

    private final Server server;

    public JettyServer() {
        this( 1905 );
    }

    public JettyServer( final int port ) {
        Log.setLog( new Log4j2Bridge( JettyServer.class ) );
        this.server = new Server( port );
        this.server.setHandler( createHandlers() );
    }

    public void start() throws MDException {
        try {
            this.server.start();
        } catch ( Exception ex ) {
            throw new MDException( "Starting the server failed.", ex );
        }
    }

    public void stop() throws MDException {
        try {
            this.server.stop();
        } catch ( Exception ex ) {
            throw new MDException( "Stopping the server failed.", ex );
        }
    }

    /**
     * Wait for the server to be stopped. Checks all 2 seconds if either 'q' can be read from System.in or the server's
     * shutdown-mechanism was triggered.
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
            boolean serverStopped = this.server.isStopping() || this.server.isStopped();
            boolean quitBySysin = sysinIsOpen && getStopFromSysin();
            logger.debug( "Server active; serverStopped: {}; quitBySysin: {}", serverStopped, quitBySysin );
            if ( serverStopped || quitBySysin ) {
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

    private boolean getStopFromSysin() {
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

    private Handler createHandlers() {
        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers( new Handler[] {
            createMangaBaseHandler(), // handle the manga-related requests
            createShutdownHandler(), // handle the shutdown-requests
            createResourceHandler(), // handle the file-requests
            new DefaultHandler() // handle the left requests
        } );
        return handlerList;
    }

    private Handler createMangaBaseHandler() {
        ContextHandler contextHandler = new ContextHandler( "j" );
        contextHandler.setHandler( new MangaBaseHandler() );
        return contextHandler;
    }

    private Handler createShutdownHandler() {

        String pwConfig = new File( "conf/jetty-users.dsc" ).getAbsolutePath();
        HashLoginService loginService = new HashLoginService( "MangaDownloader-Realm", pwConfig );
        this.server.addBean( loginService, true );

        ConstraintSecurityHandler secHandler = new ConstraintSecurityHandler();
        secHandler.setAuthenticator( new BasicAuthenticator() );

        ConstraintMapping cm = new ConstraintMapping();
        final Constraint userConstraint = new Constraint( Constraint.__BASIC_AUTH, "**" );
        userConstraint.setAuthenticate( true );
        cm.setConstraint( userConstraint );
        cm.setPathSpec( "/shutdown/*" );

        secHandler.setConstraintMappings( Arrays.asList( cm ) );
        secHandler.setHandler( new ShutdownHandler( SUPER_SECRET_MAGIC_TOKEN_FOR_SHUTDOWN, false, true ) );
        secHandler.setLoginService( loginService );

        return secHandler;
    }

    private Handler createResourceHandler() {
        ResourceHandler resourceHandler = new ResourceHandler();
        // load from the jetty-folder
        resourceHandler.setResourceBase( "jetty" );
        // enable caching via etags
        resourceHandler.setEtags( true );
        return resourceHandler;
    }

}
