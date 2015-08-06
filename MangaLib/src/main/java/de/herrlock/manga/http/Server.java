package de.herrlock.manga.http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.IOUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.http.location.Location;
import de.herrlock.manga.http.location.NotFoundLocation;
import de.herrlock.manga.http.response.Response;
import de.herrlock.manga.http.response.ServerExceptionResponse;

/**
 * @author HerrLock
 */
public class Server extends ServerSocket implements Runnable {

    private static final Logger logger = LogManager.getLogger();

    private final Map<String, Location> locations = new HashMap<>();
    private final Location location404 = new NotFoundLocation();

    public Server() throws IOException {
        this( 1905 );
    }

    public Server( int port ) throws IOException {
        super( port );
    }

    @Override
    public void run() {
        logger.entry();
        try {
            logger.info( "started, request data at http://localhost:{}/", this.getLocalPort() );
            while ( true ) {
                Socket socket = this.accept();
                System.out.println( socket );
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
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }

    public Response handleXHR( URL url ) {
        String path = url.getPath();
        logger.info( "Path: {}", path );
        String query = url.getQuery();
        logger.info( "Query: {}", query );

        Location loc = this.location404;
        for ( Entry<String, Location> entry : this.locations.entrySet() ) {
            if ( entry.getKey().equals( path ) ) {
                loc = entry.getValue();
                break;
            }
        }
        try {
            return loc.handleXHR( url );
        } catch ( ServerException ex ) {
            logger.error( ex );
            return new ServerExceptionResponse( ex );
        }
    }

    public void registerLocation( Location loc ) {
        logger.info( "register \"/{}\"", loc.getPath() );
        String path = "/" + loc.getPath();
        if ( this.locations.containsKey( path ) ) {
            throw new RuntimeException( path + " already registered" );
        }
        this.locations.put( path, loc );
    }
}
