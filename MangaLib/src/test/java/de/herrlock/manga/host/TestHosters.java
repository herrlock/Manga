package de.herrlock.manga.host;

import java.io.IOException;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;

public class TestHosters {
    @Test
    public void testGetHostByURL1() throws IOException {
        URL url = new URL( "http://mangapanda.com" );
        Hoster hoster = Hosters.tryGetHostByURL( url );
        Assert.assertEquals( "Mangapanda", hoster.getName() );
    }

    @Test
    public void testGetHostByURL2() throws IOException {
        URL url = new URL( "http://mangafox.me" );
        Hoster hoster = Hosters.tryGetHostByURL( url );
        Assert.assertEquals( "Mangafox", hoster.getName() );
    }

    @Test
    public void testEquals() throws IOException {
        Hoster hoster1 = Hosters.tryGetHostByURL( new URL( "http://mangapanda.com" ) );
        Hoster hoster2 = Hosters.tryGetHostByURL( new URL( "http://mangapanda.com" ) );
        boolean equals = hoster1.equals( hoster2 );
        Assert.assertTrue( equals );
    }

    @Test
    public void testNotEquals() throws IOException {
        Hoster hoster1 = Hosters.tryGetHostByURL( new URL( "http://mangapanda.com" ) );
        Assert.assertNotNull( hoster1 );
    }

    @Test
    public void testNotEquals2() throws IOException {
        Hoster hoster1 = Hosters.tryGetHostByURL( new URL( "http://mangapanda.com" ) );
        Object hoster2 = new Object();
        boolean equals = hoster1.equals( hoster2 );
        Assert.assertFalse( equals );
    }

    @Test
    public void testNotEquals3() throws IOException {
        Hoster hoster1 = Hosters.tryGetHostByURL( new URL( "http://mangapanda.com" ) );
        Hoster hoster2 = Hosters.tryGetHostByURL( new URL( "http://mangafox.me" ) );
        boolean equals = hoster1.equals( hoster2 );
        Assert.assertFalse( equals );
    }

}
