package de.herrlock.manga.html;

import org.junit.Assert;
import org.junit.Test;

public class TestViewPage {

    @Test
    public void testFormatManganame1() {
        String manganame = ViewPage.formatManganame( "naruto" );
        Assert.assertEquals( "naruto", manganame );
    }

    @Test
    public void testFormatManganame2() {
        String manganame = ViewPage.formatManganame( "naruto_" );
        Assert.assertEquals( "naruto", manganame );
    }

    @Test
    public void testFormatManganame3() {
        String manganame = ViewPage.formatManganame( "naruto_1337" );
        Assert.assertEquals( "naruto", manganame );
    }

    @Test
    public void testFormatManganame4() {
        String manganame = ViewPage.formatManganame( "naruto_1337_asd" );
        Assert.assertEquals( "naruto 1337 asd", manganame );
    }

}
