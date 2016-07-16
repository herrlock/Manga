package de.herrlock.manga.tomcat.servlet;

import static org.junit.Assert.assertEquals;

import javax.ws.rs.core.Response;

import org.junit.Test;

import de.herrlock.manga.tomcat.servlet.ImageService.BGObject;
import de.herrlock.manga.tomcat.servlet.ImageService.Image;

/**
 * @author HerrLock
 */
public class ImageServiceTest {
    private final ImageService imageService = new ImageService();

    @Test
    public void getImage() {
        Response response = this.imageService.getImage();
        assertEquals( 200, response.getStatus() );
    }

    @Test
    public void getBGObjectWhite() {
        BGObject bgObject = new BGObject( Image.SAKURASOU_SHIINA );
        assertEquals( "#ffffff", bgObject.getColor() );
        assertEquals( "/res/bg/sakurasou_shiina.jpg", bgObject.getPath() );
    }

    @Test
    public void getBGObjectBlack() {
        BGObject bgObject = new BGObject( Image.ONEPIECE_JOLLYROGER );
        assertEquals( "#000000", bgObject.getColor() );
        assertEquals( "/res/bg/onepiece_jollyroger.jpg", bgObject.getPath() );
    }

}
