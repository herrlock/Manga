package de.herrlock.manga.downloader.pmc;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.Collection;
import java.util.Comparator;

/**
 * @author HerrLock
 *
 * @param <K>
 *            the type of the key
 * @param <V>
 *            the type of the value
 */
public final class ImmutableEntryList<K, V> extends EntryList<K, V> {

    /**
     * @param c
     *            the collection to copy the values from
     */
    public ImmutableEntryList( Collection<? extends SimpleImmutableEntry<K, V>> c ) {
        super( c );
    }

    /**
     * @param e
     *            the EntryList to copy the values from
     */
    public ImmutableEntryList( EntryList<K, V> e ) {
        super( e );
    }

    /**
     * {@link UnsupportedOperationException}
     */
    @Override
    public void addEntry( K key, V value ) {
        throw new UnsupportedOperationException();
    }

    /**
     * {@link UnsupportedOperationException}
     */
    @Override
    public void sort( Comparator<? super SimpleImmutableEntry<K, V>> c ) {
        throw new UnsupportedOperationException();
    }
}
