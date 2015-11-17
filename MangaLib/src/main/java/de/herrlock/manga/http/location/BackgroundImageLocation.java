package de.herrlock.manga.http.location;

import java.io.InputStream;
import java.net.URL;
import java.util.Date;
import java.util.Random;

import de.herrlock.manga.http.Server;
import de.herrlock.manga.http.response.InputStreamResponse;
import de.herrlock.manga.http.response.Response;

/**
 * A Location for the background-image, returns a random image
 * 
 * @author HerrLock
 */
public final class BackgroundImageLocation extends Location {

    /**
     * Create a new BackgroundImageLocation
     */
    public BackgroundImageLocation() {
        super( "/background.jpg" );
    }

    @Override
    protected Response handleXHR( final URL url ) {
        return new InputStreamResponse( Image.getRandom().getStream(), "image" );
    }

    static enum Image {
        NAUSICAA( "nausicaa.jpg" ), ONEPIECE( "onepiece.jpg" );

        private static final Random RANDOM = new Random( new Date().getTime() );
        private final String filename;

        private Image( final String filename ) {
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
