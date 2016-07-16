package de.herrlock.manga.html;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import org.junit.Test;

import de.herrlock.manga.util.StaticMethods;

public class TestViewPageConstants {

    @Test
    public void testFileFilter1() {
        assertTrue( ViewPageConstants.isDirectoryFilter.accept( new File( "." ) ) );
    }

    @Test
    public void testFileFilter2() {
        assertFalse( ViewPageConstants.isDirectoryFilter.accept( new File( "./build.gradle" ) ) );
    }

    @Test
    public void testFileFilter3() {
        assertFalse( ViewPageConstants.isDirectoryFilter.accept( new File( "./doesnot.exist" ) ) );
    }

    @Test
    public void testNumericComparator1() {
        assertTrue( ViewPageConstants.numericFilenameComparator.compare( new File( "2" ), new File( "10" ) ) < 0 );
    }

    @Test
    public void testNumericComparator2() {
        assertTrue( ViewPageConstants.numericFilenameComparator.compare( new File( "2.5" ), new File( "10" ) ) < 0 );
    }

    @Test
    public void testIntegerEntryComparator1() {
        Entry<Integer, String> entry1 = new SimpleEntry<>( 2, "first entry" );
        Entry<Integer, String> entry2 = new SimpleEntry<>( 10, "second entry" );
        assertTrue( ViewPageConstants.integerEntryComparator.compare( entry1, entry2 ) < 0 );
    }

    @Test
    public void testConstructor() throws ReflectiveOperationException {
        StaticMethods.callPrivateConstructor( ViewPageConstants.class );
    }

}
