package de.herrlock.manga.downloader.pmc;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import de.herrlock.manga.util.Constants;

public class TestEntryList {

    @Test
    public void testSize1() {
        EntryList<String, String> list = new EntryList<>();
        list.addEntry( "key", "value" );
        Assert.assertEquals( 1, list.size() );
    }

    @Test
    public void testSize2() {
        EntryList<String, String> list = new EntryList<>( 3 );
        list.addEntry( "key", "value" );
        Assert.assertEquals( 1, list.size() );
    }

    @Test
    public void testSize3() {
        EntryList<String, String> list1 = new EntryList<>();
        list1.addEntry( "key1", "value1" );
        EntryList<String, String> list = new EntryList<>( list1 );
        list.addEntry( "key", "value" );
        Assert.assertEquals( 2, list.size() );
    }

    @Test
    public void testSort() {
        EntryList<String, String> list = new EntryList<>();
        list.addEntry( "2", "value2" );
        list.addEntry( "1", "value1" );
        // check elements
        Iterator<SimpleImmutableEntry<String, String>> iterator1 = list.iterator();
        Assert.assertEquals( "value2", iterator1.next().getValue() );
        Assert.assertEquals( "value1", iterator1.next().getValue() );
        // sort
        list.sort( EntryList.getStringComparator( Constants.STRING_NUMBER_COMPARATOR ) );
        // check new order
        Iterator<SimpleImmutableEntry<String, String>> iterator2 = list.iterator();
        Assert.assertEquals( "value1", iterator2.next().getValue() );
        Assert.assertEquals( "value2", iterator2.next().getValue() );
    }

    @Test
    public void testImmutableEntryList1() {
        EntryList<String, String> list1 = new EntryList<>();
        list1.addEntry( "key1", "value1" );
        EntryList<String, String> list = new ImmutableEntryList<>( list1 );
        Assert.assertEquals( 1, list.size() );
    }

    @Test
    public void testImmutableEntryList2() {
        SimpleImmutableEntry<String, String> entry = new SimpleImmutableEntry<>( "key", "value" );
        List<SimpleImmutableEntry<String, String>> collection = Arrays.asList( entry );
        EntryList<String, String> list = new ImmutableEntryList<>( collection );
        Assert.assertEquals( 1, list.size() );
    }

    @Test( expected = UnsupportedOperationException.class )
    public void testImmutableEntryListAddEntry() {
        EntryList<String, String> oldList = new EntryList<>();
        EntryList<String, String> list = new ImmutableEntryList<>( oldList );
        list.addEntry( "key1", "value1" );
        Assert.fail( "previous method should have thrown an exception" );
    }

    @Test( expected = UnsupportedOperationException.class )
    public void testImmutableEntryListSort() {
        EntryList<String, String> oldList = new EntryList<>();
        EntryList<String, String> list = new ImmutableEntryList<>( oldList );
        list.sort( null );
        Assert.fail( "previous method should have thrown an exception" );
    }
}
