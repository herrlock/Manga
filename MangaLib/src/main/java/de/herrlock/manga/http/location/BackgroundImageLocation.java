package de.herrlock.manga.http.location;

import java.io.IOException;
import java.io.StringWriter;
import java.net.URL;

import org.apache.commons.io.IOUtils;

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
            final StringWriter writer = new StringWriter();
            IOUtils.copy( Server.class.getResourceAsStream( "background.jpg" ), writer );
            return new Response() {
                {
                    setCode( 200 );
                }

                @Override
                protected Object getData() {
                    return writer.toString();
                }

                @Override
                protected String getContentType() {
                    return "image/jpg";
                }
            };

        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }
}
