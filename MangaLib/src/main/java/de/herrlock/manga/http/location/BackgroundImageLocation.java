package de.herrlock.manga.http.location;

import java.net.URL;

import de.herrlock.manga.http.ImageResponse;
import de.herrlock.manga.http.Response;
import de.herrlock.manga.http.Server;

/**
 * @author HerrLock
 */
public class BackgroundImageLocation extends Location {

    public BackgroundImageLocation() {
        super( "background.jpg" );
    }

    @Override
    public Response handleXHR( URL url ) {
        return new ImageResponse( Server.class.getResourceAsStream( "background.jpg" ) );
    }
}
