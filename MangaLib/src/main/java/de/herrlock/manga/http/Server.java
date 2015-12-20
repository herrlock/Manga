package de.herrlock.manga.http;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpRequestInterceptor;
import org.apache.http.RequestLine;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.http.location.BackgroundImageLocation;
import de.herrlock.manga.http.location.IndexHtmlLocation;
import de.herrlock.manga.http.location.JQueryLocation;
import de.herrlock.manga.http.location.NotFoundLocation;
import de.herrlock.manga.http.location.StartDownloadLocation;

public class Server {

    private static final Logger logger = LogManager.getLogger();

    private final HttpRequestInterceptor STOP_SERVER_INTERCEPTOR = new HttpRequestInterceptor() {
        @Override
        public void process( final HttpRequest request, final HttpContext context ) throws HttpException, IOException {
            RequestLine requestLine = request.getRequestLine();
            if ( "/stopServer".equals( requestLine.getUri() ) ) {
                Server.this.stop();
            }
        }
    };

    private final Thread serverShutdownThread = new Thread() {
        @Override
        public void run() {
            Server.this.httpServer.shutdown( 5, TimeUnit.SECONDS );
        }
    };

    private final HttpServer httpServer;

    public static void startDefaultServer() throws IOException {
        createDefault().start();
    }

    public static Server createDefault() {
        return new Server( new IndexHtmlLocation(), new JQueryLocation(), new BackgroundImageLocation(),
            new StartDownloadLocation(), new NotFoundLocation() );
    }

    public Server( final HttpRequestHandlerWrapper... handlerWrapper ) {
        this( 1905, handlerWrapper );
    }

    public Server( final int port, final HttpRequestHandlerWrapper... handlerWrapper ) {
        ServerBootstrap serverBootstrap = ServerBootstrap.bootstrap().setListenerPort( port );
        serverBootstrap.addInterceptorFirst( this.STOP_SERVER_INTERCEPTOR );
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
