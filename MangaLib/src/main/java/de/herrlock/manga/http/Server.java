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

/**
 * @author HerrLock
 */
public class Server extends ServerSocket implements Runnable {

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
        try {
            System.out.println( "started" );
            while ( true ) {
                Socket socket = this.accept();
                System.out.println( socket );
                try ( InputStream in = socket.getInputStream() ) {
                    String requestPart = IOUtils.lineIterator( in, StandardCharsets.UTF_8 ).nextLine();
                    String[] split = requestPart.split( " ", 3 );
                    URL url = new URL( "http://localhost" + split[1] );

                    Response result = handleXHR( url );
                    try ( OutputStream out = socket.getOutputStream() ) {
                        IOUtils.write( result.toString(), out );
                    }
                }
            }
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }

    public Response handleXHR( URL url ) {
        String path = url.getPath();
        System.out.println( "Path: " + path );
        String query = url.getQuery();
        System.out.println( "Query: " + query );

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
            System.err.println( ex );
            return new ServerExceptionResponse( ex );
        }
    }

    public void registerLocation( Location loc ) {
        System.out.println( "register \"/" + loc.getPath() + "\"" );
        String path = "/" + loc.getPath();
        if ( this.locations.containsKey( path ) ) {
            throw new RuntimeException( path + " already registered" );
        }
        this.locations.put( path, loc );
    }
}
