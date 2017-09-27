package de.herrlock.manga.viewpage;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.junit.Assert;
import org.junit.Test;

import de.herrlock.manga.util.StaticMethods;

public class TestViewPageConstants {

    @Test
    public void testFormatManganame1() {
        String manganame = ViewPageConstants.formatManganame( "naruto" );
        Assert.assertEquals( "naruto", manganame );
    }

    @Test
    public void testFormatManganame2() {
        String manganame = ViewPageConstants.formatManganame( "naruto_" );
        Assert.assertEquals( "naruto", manganame );
    }

    @Test
    public void testFormatManganame3() {
        String manganame = ViewPageConstants.formatManganame( "naruto_1337" );
        Assert.assertEquals( "naruto", manganame );
    }

    @Test
    public void testFormatManganame4() {
        String manganame = ViewPageConstants.formatManganame( "naruto_1337_asd" );
        Assert.assertEquals( "naruto 1337 asd", manganame );
    }

    @Test
    public void testFileFilter1() {
        assertTrue( new ViewPageConstants.FileIsDirectoryFilter().accept( new File( "." ) ) );
    }

    @Test
    public void testFileFilter2() {
        assertFalse( new ViewPageConstants.FileIsDirectoryFilter().accept( new File( "./build.gradle" ) ) );
    }

    @Test
    public void testFileFilter3() {
        assertFalse( new ViewPageConstants.FileIsDirectoryFilter().accept( new File( "./doesnot.exist" ) ) );
    }

    @Test
    public void testNumericComparator1() {
        assertTrue( ViewPageConstants.NUMERIC_FILENAME_COMPARATOR.compare( new File( "2" ), new File( "10" ) ) < 0 );
    }

    @Test
    public void testNumericComparator2() {
        assertTrue( ViewPageConstants.NUMERIC_FILENAME_COMPARATOR.compare( new File( "2.5" ), new File( "10" ) ) < 0 );
    }

    @Test
    public void testIntegerEntryComparator1() {
        Entry<Integer, String> entry1 = new SimpleEntry<>( 2, "first entry" );
        Entry<Integer, String> entry2 = new SimpleEntry<>( 10, "second entry" );
        assertTrue( ViewPageConstants.INTEGER_ENTRY_COMPARATOR.compare( entry1, entry2 ) < 0 );
    }

    @Test
    public void testConstructor() throws ReflectiveOperationException {
        StaticMethods.callPrivateConstructor( ViewPageConstants.class );
    }

}
