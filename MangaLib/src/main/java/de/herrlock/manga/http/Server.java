package de.herrlock.manga.http;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Server {

    private static final Logger logger = LogManager.getLogger();

    private final Thread serverShutdownThread = new Thread() {
        @Override
        public void run() {
            Server.this.httpServer.shutdown( 5, TimeUnit.SECONDS );
        }
    };
    private final HttpServer httpServer;

    public Server( final HttpRequestHandlerWrapper... handlerWrapper ) {
        this( 1905, handlerWrapper );
    }

    public Server( final int port, final HttpRequestHandlerWrapper... handlerWrapper ) {
        ServerBootstrap serverBootstrap = ServerBootstrap.bootstrap().setListenerPort( port );
        for ( HttpRequestHandlerWrapper handler : handlerWrapper ) {
            serverBootstrap.registerHandler( handler.getPattern(), handler );
        }
        this.httpServer = serverBootstrap.create();
    }

    public boolean running() {
        return this.httpServer.getInetAddress() != null;
    }

    /**
     * starts the httpServer. returns immediately if it is already running.
     * 
     * @throws IOException
     *             from {@link HttpServer#start()}
     */
    public void start() throws IOException {
        if ( running() ) {
            return;
        }
        logger.info( "starting server" );
        this.httpServer.start();
        Runtime.getRuntime().addShutdownHook( this.serverShutdownThread );
    }

    /**
     * stops the server. returns immediately if the server has not been created
     */
    public void stop() {
        if ( !running() ) {
            return;
        }
        logger.info( "stopping server" );
        Runtime.getRuntime().removeShutdownHook( this.serverShutdownThread );
        this.httpServer.stop();
    }

}
