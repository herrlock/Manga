package de.herrlock.manga.http.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import de.herrlock.manga.http.Server;
import de.herrlock.manga.http.response.Response;
import de.herrlock.manga.http.response.TextResponse;

/**
 * A Location that returns jquery
 * 
 * @author HerrLock
 */
public class JQueryLocation extends Location {

    /**
     * Create a new JQueryLocation
     */
    public JQueryLocation() {
        super( "jquery.js" );
    }

    @Override
    public Response handleXHR( URL url ) {
        TextResponse response = new TextResponse();

        try ( BufferedReader reader = new BufferedReader( new InputStreamReader(
            Server.class.getResourceAsStream( "/de/herrlock/manga/http/jquery-2.1.3.min.js" ), StandardCharsets.UTF_8 ) ) ) {
            StringBuilder sb = new StringBuilder();
            String nextline;
            while ( ( nextline = reader.readLine() ) != null ) {
                sb.append( nextline );
            }
            response.setText( sb.toString() );
            response.setCode( 200 );
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
        return response;
    }

}
