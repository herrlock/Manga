package de.herrlock.manga.http.jetty;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.ContextHandler;
import org.eclipse.jetty.server.handler.DebugHandler;
import org.eclipse.jetty.server.handler.DefaultHandler;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.server.handler.ShutdownHandler;
import org.eclipse.jetty.util.log.Log;

import de.herrlock.manga.http.jetty.handlers.MangaBaseHandler;
import de.herrlock.manga.http.jetty.log.Log4j2Bridge;

public final class JettyEmbeddedRunner {

    /**
     * This must be kept a secret !!!1!!11
     */
    private static final String SUPER_SECRET_MAGIC_TOKEN_FOR_SHUTDOWN = "avadakedavra";

    public static void main( final String... args ) throws Exception {
        new JettyEmbeddedRunner().startServer();
    }

    public void startServer() throws Exception {
        Log.setLog( new Log4j2Bridge( JettyEmbeddedRunner.class.getName() ) );
        Server server = new Server( 1905 );
        server.setHandler( createHandlers() );
        server.start();
    }

    private Handler createHandlers() {
        HandlerList handlerList = new HandlerList();
        handlerList.setHandlers( new Handler[] {
            createMangaBaseHandler(), // handle the manga-related requests
            createShutdownHandler(), // handle the shutdown-requests
            createResourceHandler(), // handle the file-requests
            new DefaultHandler() // handle the left requests
        } );
        DebugHandler debugHandler = new DebugHandler();
        debugHandler.setHandler( handlerList );
        debugHandler.setOutputStream( System.out );
        return debugHandler;
    }

    private Handler createMangaBaseHandler() {
        ContextHandler contextHandler = new ContextHandler( "j" );
        contextHandler.setHandler( new MangaBaseHandler() );
        return contextHandler;
    }

    private Handler createShutdownHandler() {
        return new ShutdownHandler( SUPER_SECRET_MAGIC_TOKEN_FOR_SHUTDOWN, false, true );
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
