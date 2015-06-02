package de.herrlock.manga.http.location;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import de.herrlock.manga.http.Response;
import de.herrlock.manga.http.Server;
import de.herrlock.manga.http.TextResponse;

/**
 * @author HerrLock
 */
public class JQueryLocation extends Location {

    public JQueryLocation() {
        super( "jquery.js" );
    }

    @Override
    public Response handleXHR( URL url ) {
        TextResponse response = new TextResponse();

        try ( BufferedReader reader = new BufferedReader( new InputStreamReader(
            Server.class.getResourceAsStream( "/de/herrlock/manga/html/jquery-2.1.3.min.js" ), StandardCharsets.UTF_8 ) ) ) {
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
