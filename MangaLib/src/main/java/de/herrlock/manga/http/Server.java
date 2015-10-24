package de.herrlock.manga.http;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.exceptions.MyException;
import de.herrlock.manga.http.location.Location;
import de.herrlock.manga.http.location.NotFoundLocation;
import de.herrlock.manga.http.response.Response;
import de.herrlock.manga.http.response.ServerExceptionResponse;
import de.herrlock.manga.http.response.TextResponse;
import de.herrlock.manga.ui.log.LogWindow;

/**
 * @author HerrLock
 */
public final class Server extends ServerSocket implements Runnable {

    private static final Logger logger = LogManager.getLogger();

    private final Map<String, Location> locations = new HashMap<>();
    private final Location location404 = new NotFoundLocation();
    private boolean active = true;

    /**
     * Creates a Server that lets a {@link ServerSocket} listen to the port 1905
     * 
     * @throws IOException
     *             thrown by {@link Server#Server(int)}
     */
    public Server() throws IOException {
        this( 1905 );
    }

    /**
     * Creates a Server that lets a {@link ServerSocket} listen to the given port
     * 
     * @param port
     *            the port to listen to
     * @throws IOException
     *             thrown by {@link ServerSocket#ServerSocket(int)}
     */
    public Server( final int port ) throws IOException {
        super( port );
    }

    @Override
    public void run() {
        logger.entry();
        try {
            logger.info( "started, request data at http://localhost:{}/", this.getLocalPort() );
            while ( this.active ) {
                logger.debug( "socket waiting" );
                Socket socket = this.accept();
                logger.debug( socket );
                try ( InputStream in = socket.getInputStream() ) {
                    String requestPart = IOUtils.lineIterator( in, StandardCharsets.UTF_8 ).nextLine();
                    String[] split = requestPart.split( " ", 3 );
                    URL url = new URL( "http://localhost" + split[1] );

                    Response res = handleXHR( url );
                    try ( OutputStream out = socket.getOutputStream() ) {
                        String httpHeader = res.createHTTPHeader();
                        IOUtils.write( httpHeader, out );
                        byte[] data = res.getData();
                        IOUtils.write( data, out );
                    }
                }
            }
        } catch ( final SocketException ex ) {
            logger.info( "stopping server" );
        } catch ( final IOException ex ) {
            throw new MyException( ex );
        } finally {
            logger.info( "server stopped" );
        }
    }

    /**
     * 
     */
    public void stopServer() {
        try ( Socket s = new Socket( "localhost", 1905 ) ) {
            try ( BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter( s.getOutputStream(), StandardCharsets.UTF_8 ) ) ) {
                writer.write( "GET /stopServer HTTP/1.1" );
            }
        } catch ( final IOException ex ) {
            throw new ServerException( ex );
        }
    }

    private Response handleXHR( final URL url ) {
        String path = url.getPath();
        logger.debug( "Path: {}", path );
        String query = url.getQuery();
        logger.debug( "Query: {}", query );

        Location loc = this.location404;
        for ( Entry<String, Location> entry : this.locations.entrySet() ) {
            if ( entry.getKey().equals( path ) ) {
                loc = entry.getValue();
                break;
            }
        }
        try {
            return loc.handleXHR( url );
        } catch ( final ServerException ex ) {
            logger.error( ex );
            return new ServerExceptionResponse( ex );
        } catch ( final CloseServerException ex ) {
            logger.info( "stopping Server" );
            this.active = false;
            LogWindow.dispose();
            return new TextResponse( 200, "stoppedServer" );
        }
    }

    /**
     * adds a {@link Location} to request data from to this server
     * 
     * @param loc
     *            the {@link Location} to add
     * @throws IllegalArgumentException
     *             in case the path is already registered
     */
    public void registerLocation( final Location loc ) throws IllegalArgumentException {
        logger.debug( "register \"/{}\"", loc.getPath() );
        String path = "/" + loc.getPath();
        if ( this.locations.containsKey( path ) ) {
            throw new IllegalArgumentException( path + " already registered" );
        }
        this.locations.put( path, loc );
    }
}
