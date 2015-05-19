package de.herrlock.manga.http;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

/**
 * @author HerrLock
 */
public class DefaultLocation extends Location {

    public DefaultLocation() {
        super( "md" );
    }

    @Override
    public Response handleXHR( URL url ) {
        DocumentResponse response = new DocumentResponse();
        try ( InputStream indexhtml = Server.class.getResourceAsStream( "index.html" ) ) {
            Document document = Jsoup.parse( indexhtml, null, "http://localhost" );
            response.setCode( 200 );
            response.setDocument( document );
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
        return response;
    }

}
