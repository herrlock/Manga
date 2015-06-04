package de.herrlock.manga.http.location;

import java.io.IOException;
import java.net.URL;

import de.herrlock.manga.http.Server;
import de.herrlock.manga.http.ServerException;
import de.herrlock.manga.http.response.ImageResponse;
import de.herrlock.manga.http.response.Response;

/**
 * @author HerrLock
 */
public class BackgroundImageLocation extends Location {

    public BackgroundImageLocation() {
        super( "background.jpg" );
    }

    @Override
    public Response handleXHR( URL url ) {
        try {
            return new ImageResponse( Server.class.getResourceAsStream( "background.jpg" ) );
        } catch ( IOException ex ) {
            throw new ServerException( ex );
        }
    }
}
