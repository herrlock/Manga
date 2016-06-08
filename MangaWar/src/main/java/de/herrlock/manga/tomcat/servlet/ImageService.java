package de.herrlock.manga.tomcat.servlet;

import java.net.URI;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * A service to serve the path to a random image from the enum {@link Image}
 * 
 * @author HerrLock
 */
@Path( "images" )
public class ImageService {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Returns a 303-response that links to an image that was randomly chosen from the enum {@link Image}
     * 
     * @return a 303-response containing the random link
     */
    @GET
    @Path( "background.jpg" )
    public Response getImage() {
        logger.traceEntry();
        String imagePath = Image.getRandom().getPath();
        logger.debug( "redir to: {}", imagePath );
        return Response.seeOther( URI.create( imagePath ) ).build();
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
        CITS_ROBOT( "cits_robot.jpg" ),
        /**
         * Sakura trees (the castle in the sky)
         */
        CITS_SAKURA( "cits_sakura.jpg" ),
        /**
         * Kodama (mononoke hime)
         */
        MG_KODAMA( "mh_kodama.jpg" ),
        /**
         * Many small narutos (naruto)
         */
        NARUTO_NARUTOS( "naruto_narutos.jpg" ),
        /**
         * Naruto and Sasuke (naruto)
         */
        NARUTO_NS( "naruto_ns.jpg" ),
        /**
         * Nausica&auml; (the valley of the wind)
         */
        NAUSICAA( "nausicaa.jpg" ),
        /**
         * the jollyroger from the strawhat crew (one piece)
         */
        ONEPIECE_JOLLYROGER( "onepiece_jollyroger.jpg" ),
        /**
         * pirates from the strawhat crew(one piece)
         */
        ONEPIECE_PIRATES( "onepiece_pirates.jpg" ),
        /**
         * Mashiro Shiina (sakurasou no pet no kanojo)
         */
        SAKURASOU_SHIINA( "sakurasou_shiina.jpg" ),
        /**
         * Sanka Rea (sankarea)
         */
        SANKAREA( "sankarea.jpg" ),
        /**
         * Misaka "the railgun" Mikoto (toaru kagaku no railgun)
         */
        TKR_MISAKA( "tkr_misaka.jpg" );

        private static final Random RANDOM = new Random();
        private final String filename;

        private Image( final String filename ) {
            this.filename = filename;
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
    }
}
