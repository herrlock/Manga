package de.herrlock.manga.http.location;

import java.io.InputStream;
import java.net.URL;

import org.apache.http.entity.ContentType;

import de.herrlock.manga.http.Server;
import de.herrlock.manga.http.response.InputStreamResponse;
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
        super( "/" );
    }

    @Override
    protected Response handleXHR( final URL url ) {
        InputStream indexhtml = Server.class.getResourceAsStream( "index.html" );
        return new InputStreamResponse( indexhtml, ContentType.TEXT_HTML );
    }

}
