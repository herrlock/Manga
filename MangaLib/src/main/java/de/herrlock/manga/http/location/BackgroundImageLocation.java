package de.herrlock.manga.http.location;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;

import org.apache.commons.io.IOUtils;

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
        try {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy( Server.class.getResourceAsStream( "background.jpg" ), out );
            return new ImageResponse( out );

        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }
}
