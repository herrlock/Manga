package de.herrlock.manga.jd;

import java.io.File;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author HerrLock
 */
public class TestCrawljob {

    @Test
    public void test1() {
        Crawljob crawljob = new Crawljob( new File( "" ), "42" );
        crawljob.addCrawljobEntry( "filename", "url" );
        String ls = System.lineSeparator();
        StringBuilder sb = new StringBuilder()//
            .append( ls )//
            .append( "->NEW ENTRY<-" )//
            .append( ls )//
            .append( "text=url" )//
            .append( ls )//
            .append( "filename=filename.jpg" )//
            .append( ls )//
            .append( "downloadFolder=C:\\Users\\Jan\\workspace\\Manga\\MangaLib" )//
            .append( ls )//
            .append( "packageName=42" )//
            .append( ls )//
            .append( "addOfflineLink=true" )//
            .append( ls )//
            .append( ( char ) 0 );
        String expected = sb.toString();
        String export = crawljob.export();
        Assert.assertEquals( expected, export );
    }

    @Test
    public void testGetFilename() {
        Crawljob crawljob = new Crawljob( new File( "" ), "42" );
        Assert.assertEquals( "42.crawljob", crawljob.getFilename() );
    }
}
