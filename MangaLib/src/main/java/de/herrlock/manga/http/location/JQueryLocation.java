package de.herrlock.manga.http.location;

import java.io.InputStream;
import java.net.URL;

import de.herrlock.manga.http.Server;
import de.herrlock.manga.http.response.InputStreamResponse;
import de.herrlock.manga.http.response.Response;

/**
 * A Location that returns jquery
 * 
 * @author HerrLock
 */
public final class JQueryLocation extends Location {

    /**
     * Create a new JQueryLocation
     */
    public JQueryLocation() {
        super( "/jquery.js" );
    }

    @Override
    protected Response handleXHR( final URL url ) {
        InputStream inputStream = Server.class.getResourceAsStream( "/de/herrlock/manga/http/jquery-2.1.3.min.js" );
        return new InputStreamResponse( inputStream, "application/javascript" );
    }

}
