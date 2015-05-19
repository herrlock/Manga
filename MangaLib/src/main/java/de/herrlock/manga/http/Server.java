package de.herrlock.manga.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author HerrLock
 */
public class Server extends ServerSocket implements Runnable {

    public static void main( String[] args ) {
        try ( Server server = new Server() ) {
            Thread serverThread = new Thread( server );
            serverThread.start();

            server.registerLocation( new DefaultLocation() );
            server.registerLocation( new AddLocation() );
            server.registerLocation( new JQueryLocation() );

            serverThread.join();
        } catch ( IOException | InterruptedException ex ) {
            throw new RuntimeException( ex );
        }
    }

    private final Map<String, Location> locations = new HashMap<>();
    private final Location _404Location = new NotFoundLocation();

    public Server() throws IOException {
        super( 1905 );
    }

    @Override
    public void run() {
        try {
            System.out.println( "started" );
            while ( true ) {
                Socket socket = this.accept();
                System.out.println( socket );
                try ( BufferedReader reader = new BufferedReader( new InputStreamReader( socket.getInputStream(),
                    StandardCharsets.UTF_8 ) ) ) {
                    URL url = new URL( "http://localhost" + reader.readLine().split( " " )[1] );

                    Response result = handleXHR( url );
                    try ( BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( socket.getOutputStream(),
                        StandardCharsets.UTF_8 ) ) ) {
                        writer.write( result.toString() );
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

        Location loc = this._404Location;
        for ( Entry<String, Location> entry : this.locations.entrySet() ) {
            if ( entry.getKey().equals( path.substring( 1 ) ) ) {
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
        String path = loc.getPath();
        if ( this.locations.containsKey( path ) ) {
            throw new RuntimeException( path + " already registered" );
        }
        this.locations.put( path, loc );
    }
}
