package de.herrlock.manga.downloader.pmc;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * @author HerrLock
 */
public class EntryList<K, V> implements Iterable<SimpleImmutableEntry<K, V>> {
    private final List<SimpleImmutableEntry<K, V>> entries;

    public EntryList() {
        this.entries = new ArrayList<>();
    }

    public EntryList( int initialCapacity ) {
        this.entries = new ArrayList<>( initialCapacity );
    }

    public EntryList( EntryList<K, V> entryList ) {
        this( entryList.entries );
    }

    public EntryList( Collection<? extends SimpleImmutableEntry<K, V>> c ) {
        this.entries = new ArrayList<>( c );
    }

    public void addEntry( K key, V value ) {
        this.entries.add( new SimpleImmutableEntry<>( key, value ) );
    }

    public int size() {
        return this.entries.size();
    }

    public void sort( Comparator<? super SimpleImmutableEntry<K, V>> c ) {
        Collections.sort( this.entries, c );
    }

    @Override
    public Iterator<SimpleImmutableEntry<K, V>> iterator() {
        return this.entries.iterator();
    }

    public Comparator<SimpleImmutableEntry<String, ?>> getStringComparator( final Comparator<String> comparator ) {
        return new Comparator<SimpleImmutableEntry<String, ?>>() {
            @Override
            public int compare( SimpleImmutableEntry<String, ?> o1, SimpleImmutableEntry<String, ?> o2 ) {
                return comparator.compare( o1.getKey(), o2.getKey() );
            }
        };
    }

}
