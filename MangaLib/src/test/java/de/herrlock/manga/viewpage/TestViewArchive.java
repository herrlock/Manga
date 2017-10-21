package de.herrlock.manga.viewpage;

import org.junit.Assert;
import org.junit.Test;

public class TestViewArchive {

    @Test
    public void testPad1() {
        String string = ViewArchive.padWith0( "", 10 );
        Assert.assertEquals( "0000000000", string );
    }

    @Test
    public void testPad2() {
        String string = ViewArchive.padWith0( "Hello World", 10 );
        Assert.assertEquals( "Hello World", string );
    }

    @Test
    public void testFormatArchiveFilename1() {
        String filename = ViewArchive.toArchiveEntryFilename( null );
        Assert.assertEquals( "", filename );
    }

    @Test
    public void testFormatArchiveFilename2() {
        String filename = ViewArchive.toArchiveEntryFilename( "1.jpg" );
        Assert.assertEquals( "001.jpg", filename );
    }

    @Test
    public void testFormatArchiveFilename3() {
        String filename = ViewArchive.toArchiveEntryFilename( "03.jpg" );
        Assert.assertEquals( "003.jpg", filename );
    }

    @Test
    public void testFormatArchiveFilename4() {
        String filename = ViewArchive.toArchiveEntryFilename( "100.jpg" );
        Assert.assertEquals( "100.jpg", filename );
    }

}
