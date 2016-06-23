package de.herrlock.manga.tomcat.servlet;

import javax.ws.rs.core.Response;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author HerrLock
 */
public class ImageServletTest {
    private final ImageService imageServlet = new ImageService();

    @Test
    public void getImage() {
        Response response = this.imageServlet.getImage();
        Assert.assertEquals( 200, response.getStatus() );
    }
}
