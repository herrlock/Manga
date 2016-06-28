package de.herrlock.manga.tomcat.servlet;

import java.awt.Color;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A service to serve the path to a random image from the enum {@link Image}
 * 
 * @author HerrLock
 */
@Path( "background" )
public class ImageService {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Returns response that contains the path to a random image and a colour that matches this image as background.
     * 
     * @return A Response containins background-information
     */
    @GET
    @Produces( MediaType.APPLICATION_JSON )
    public Response getImage() {
        logger.traceEntry();
        Image image = Image.getRandom();
        BGObject bgObject = new BGObject( image );

        logger.debug( "bg-object: {}", bgObject );
        return Response.ok( bgObject ).build();
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
        CITS_ROBOT( "cits_robot.jpg", Color.BLUE ),
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
        NARUTO_NARUTOS( "naruto_narutos.jpg", Color.ORANGE ),
        /**
         * Naruto and Sasuke (naruto)
         */
        NARUTO_NS( "naruto_ns.jpg", Color.YELLOW ),
        /**
         * Nausica&auml; (the valley of the wind)
         */
        NAUSICAA( "nausicaa.jpg", new Color( 0xB9, 0x7A, 0x57 ) ),
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
        SANKAREA( "sankarea.jpg", Color.MAGENTA ),
        /**
         * Misaka "the railgun" Mikoto (toaru kagaku no railgun)
         */
        TKR_MISAKA( "tkr_misaka.jpg", Color.WHITE );

        private static final Random RANDOM = new Random();
        private final String filename;
        private final Color color;

        private Image( final String filename ) {
            this( filename, Color.BLACK );
        }

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
        public String getPath() {
            return "/res/bg/" + this.filename;
        }

        public Color getColor() {
            return this.color;
        }
    }

    public static class BGObject {
        private final Image image;

        public BGObject( final Image image ) {
            this.image = image;
        }

        public String getPath() {
            return this.image.getPath();
        }

        public String getColor() {
            Color color = this.image.getColor();
            int red = color.getRed();
            String r = ( red <= 0xf ? "0" : "" ) + Integer.toHexString( red );
            int green = color.getGreen();
            String g = ( green <= 0xf ? "0" : "" ) + Integer.toHexString( green );
            int blue = color.getBlue();
            String b = ( blue <= 0xf ? "0" : "" ) + Integer.toHexString( blue );
            return "#" + r + g + b;
        }
    }
}
