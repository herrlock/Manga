package de.herrlock.manga.http.location;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.imageio.ImageIO;

import org.apache.commons.io.IOUtils;
import org.apache.http.ParseException;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;

import de.herrlock.manga.http.location.BackgroundImageLocation.Image;
import de.herrlock.manga.http.response.Response;

public class TestLocations {
    private static final URL CONTEXT_URL;

    static {
        try {
            CONTEXT_URL = new URL( "http://localhost:1905/" );
        } catch ( MalformedURLException ex ) {
            throw new IllegalStateException( ex );
        }
    }

    @Test
    public void testIndexHtmlLocation() throws ParseException, IOException {
        Location location = new IndexHtmlLocation();
        Response response = location.handleXHR( new URL( CONTEXT_URL, location.getPattern() ) );
        String string = EntityUtils.toString( response.getEntity() );

        URL indexHtmlUrl = TestLocations.class.getResource( "/de/herrlock/manga/http/index.html" );
        String expected = IOUtils.toString( indexHtmlUrl );

        Assert.assertEquals( expected, string );
    }

    @Test
    public void testJQueryLocation() throws ParseException, IOException {
        Location location = new JQueryLocation();
        Response response = location.handleXHR( new URL( CONTEXT_URL, location.getPattern() ) );
        String string = EntityUtils.toString( response.getEntity() );

        Assert.assertTrue( string.startsWith( "/*! jQuery" ) );
    }

    @Test
    public void testNotFoundLocation() throws ParseException, IOException {
        Location location = new NotFoundLocation();
        Response response = location.handleXHR( new URL( CONTEXT_URL, location.getPattern() ) );
        Assert.assertEquals( 404, response.getStatusLine().getStatusCode() );
    }

    @Test
    public void testBackgroundImageLocation() throws ParseException, IOException {
        Location location = new BackgroundImageLocation();
        Response response = location.handleXHR( new URL( CONTEXT_URL, location.getPattern() ) );

        BufferedImage image = ImageIO.read( response.getEntity().getContent() );
        Assert.assertNotNull( image );
    }

    @Test
    public void testBackgroundImageLocationImages() {
        Image[] values = BackgroundImageLocation.Image.values();
        for ( Image image : values ) {
            Assert.assertNotNull( image.getStream() );
        }
    }

}
