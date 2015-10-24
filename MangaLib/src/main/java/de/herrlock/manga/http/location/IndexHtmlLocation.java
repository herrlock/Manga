package de.herrlock.manga.http.location;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.herrlock.manga.exceptions.MyException;
import de.herrlock.manga.http.Server;
import de.herrlock.manga.http.response.DocumentResponse;
import de.herrlock.manga.http.response.Response;

/**
 * A Location that serves as main-entrance point, the html-file shown in the browser
 * 
 * @author HerrLock
 */
public final class IndexHtmlLocation extends Location {

    /**
     * Create a new IndexHtmlLocation
     */
    public IndexHtmlLocation() {
        super( "" );
    }

    @Override
    public Response handleXHR( final URL url ) {
        DocumentResponse response = new DocumentResponse();
        try ( InputStream indexhtml = Server.class.getResourceAsStream( "index.html" ) ) {
            Document document = Jsoup.parse( indexhtml, null, "http://localhost" );
            response.setCode( 200 );
            response.setDocument( document );
        } catch ( final IOException ex ) {
            throw new MyException( ex );
        }
        return response;
    }

}
