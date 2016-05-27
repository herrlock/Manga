package de.herrlock.manga.tomcat.servlet;

import java.net.URI;
import java.util.Random;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author HerrLock
 */
@Path( "images" )
public class ImageService {
    private static final Logger logger = LogManager.getLogger();

    @GET
    @Path( "background.jpg" )
    public Response getImage() {
        logger.entry();
        String imagePath = Image.getRandom().getPath();
        logger.debug( "redir to: {}", imagePath );
        return Response.seeOther( URI.create( imagePath ) ).build();
    }

    private static enum Image {
        CITS_ROBOT( "cits_robot.jpg" ), //
        CITS_SAKURA( "cits_sakura.jpg" ), //
        MG_KODAMA( "mh_kodama.jpg" ), //
        NARUTO_NARUTOS( "naruto_narutos.jpg" ), //
        NARUTO_NSX( "naruto_ns.jpg" ), //
        NAUSICAA( "nausicaa.jpg" ), //
        ONEPIECE_JOLLYROGER( "onepiece_jollyroger.jpg" ), //
        ONEPIECE_PIRATES( "onepiece_pirates.jpg" ), //
        SAKURASOU_SHIINA( "sakurasou_shiina.jpg" ), //
        SANKAREA( "sankarea.jpg" ), //
        TKR_MISAKA( "tkr_misaka.jpg" ), //
        ;

        private static final Random RANDOM = new Random();
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

        public String getPath() {
            return "/res/bg/" + this.filename;
        }
    }
}
