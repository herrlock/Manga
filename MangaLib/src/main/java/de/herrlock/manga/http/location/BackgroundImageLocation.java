package de.herrlock.manga.http.location;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Random;

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
            return new ImageResponse( Image.getRandom().getStream() );
        } catch ( IOException ex ) {
            throw new ServerException( ex );
        }
    }

    static enum Image {
        NAUSICAA( "nausicaa.jpg" ), ONEPIECE( "onepiece.jpg" );

        private static final Random RANDOM = new Random();
        private final String filename;

        private Image( String filename ) {
            this.filename = filename;
        }

        public static Image getRandom() {
            Image[] values = Image.values();
            int length = values.length;
            int rnd = RANDOM.nextInt( length );
            return values[rnd];
        }

        public InputStream getStream() {
            return Server.class.getResourceAsStream( this.filename );
        }
    }
}
