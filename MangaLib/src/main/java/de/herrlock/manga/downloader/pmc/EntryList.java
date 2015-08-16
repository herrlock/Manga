package de.herrlock.manga.downloader.pmc;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;

/**
 * @author HerrLock
 */
public class EntryList<K, V> extends ArrayList<SimpleImmutableEntry<K, V>> {
    private static final long serialVersionUID = 1L;

    public EntryList() {
        super();
    }

    public EntryList( int initialCapacity ) {
        super( initialCapacity );
    }

    public EntryList( Collection<SimpleImmutableEntry<K, V>> c ) {
        super( c );
    }

    public void addEntry( K key, V value ) {
        this.add( new SimpleImmutableEntry<>( key, value ) );
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
