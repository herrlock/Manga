package de.herrlock.manga.http.location;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.herrlock.manga.http.Server;
import de.herrlock.manga.http.response.DocumentResponse;
import de.herrlock.manga.http.response.Response;

/**
 * @author HerrLock
 */
public class IndexHtmlLocation extends Location {

    public IndexHtmlLocation() {
        super( "" );
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
