package de.herrlock.manga.http.jetty.handlers;

import java.awt.Color;
import java.io.IOException;
import java.util.Random;

import javax.json.Json;
import javax.json.JsonObject;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.common.net.MediaType;

/**
 * @author HerrLock
 */
public class ReturnImageHandler extends AbstractHandler {

    public static final String PREFIX_PATH = "background";

    @Override
    public void handle( final String target, final Request baseRequest, final HttpServletRequest request,
        final HttpServletResponse response ) throws IOException, ServletException {

        Image image = Image.getRandom();

        JsonObject object = Json.createObjectBuilder() //
            .add( "path", image.getFilename() ) //
            .add( "colour", getHtmlColour( image.getColor() ) ) //
            .build();
        Json.createWriter( response.getOutputStream() ).writeObject( object );
        response.setContentType( MediaType.JSON_UTF_8.toString() );
        response.setStatus( HttpServletResponse.SC_OK );

        baseRequest.setHandled( true );
    }

    private static String getHtmlColour( final Color color ) {
        int red = color.getRed(), green = color.getGreen(), blue = color.getBlue();
        return String.format( "#%02x%02x%02x", red, green, blue );
    }

    /**
     * All images that can be chosen as background in the web-gui.
     * 
     * @author HerrLock
     */
    public static enum Image {
        /**
         * a robot (the castle in the sky)
         */
        CITS_ROBOT( "cits_robot.jpg", new Color( 0x55, 0x88, 0xFF ) ),
        /**
         * Sakura trees (the castle in the sky)
         */
        CITS_SAKURA( "cits_sakura.jpg", Color.WHITE ),
        /**
         * Kodama (mononoke hime)
         */
        MG_KODAMA( "mh_kodama.jpg", Color.BLACK ),
        /**
         * Many small narutos (naruto)
         */
        NARUTO_NARUTOS( "naruto_narutos.jpg", new Color( 0xFF, 0x99, 0x00 ) ),
        /**
         * Naruto and Sasuke (naruto)
         */
        NARUTO_NS( "naruto_ns.jpg", new Color( 0xFF, 0xCC, 0x00 ) ),
        /**
         * Nausica&auml; (the valley of the wind)
         */
        NAUSICAA( "nausicaa.jpg", new Color( 0xDD, 0xAA, 0x44 ) ),
        /**
         * the jollyroger from the strawhat crew (one piece)
         */
        ONEPIECE_JOLLYROGER( "onepiece_jollyroger.jpg", Color.BLACK ),
        /**
         * pirates from the strawhat crew(one piece)
         */
        ONEPIECE_PIRATES( "onepiece_pirates.jpg", Color.BLACK ),
        /**
         * Mashiro Shiina (sakurasou no pet no kanojo)
         */
        SAKURASOU_SHIINA( "sakurasou_shiina.jpg", Color.WHITE ),
        /**
         * Sanka Rea (sankarea)
         */
        SANKAREA( "sankarea.jpg", new Color( 0xCC, 0x66, 0x99 ) ),
        /**
         * Misaka "the railgun" Mikoto (toaru kagaku no railgun)
         */
        TKR_MISAKA( "tkr_misaka.jpg", Color.WHITE );

        private static final Random RANDOM = new Random();
        private final String filename;
        private final Color color;

        private Image( final String filename, final Color color ) {
            this.filename = filename;
            this.color = color;
        }

        /**
         * Returns a random Image from this enum's values.
         * 
         * @return a random Image
         */
        public static Image getRandom() {
            Image[] values = Image.values();
            int length = values.length;
            int rnd = RANDOM.nextInt( length );
            return values[rnd];
        }

        /**
         * Returns the path of this image
         * 
         * @return a path that should lead to the actual image.
         */
        public String getFilename() {
            return this.filename;
        }

        public Color getColor() {
            return this.color;
        }
    }

}
