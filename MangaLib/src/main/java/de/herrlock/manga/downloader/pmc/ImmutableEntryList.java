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
    public ImmutableEntryList( final Collection<? extends SimpleImmutableEntry<K, V>> c ) {
        super( c );
    }

    /**
     * @param e
     *            the EntryList to copy the values from
     */
    public ImmutableEntryList( final EntryList<K, V> e ) {
        super( e );
    }

    /**
     * {@link UnsupportedOperationException}
     */
    @Override
    public void addEntry( final K key, final V value ) {
        throw new UnsupportedOperationException( "adding an entry to an immutable list is prohibited" );
    }

    /**
     * {@link UnsupportedOperationException}
     */
    @Override
    public void sort( final Comparator<? super SimpleImmutableEntry<K, V>> c ) {
        throw new UnsupportedOperationException( "sorting an immutable list is prohibited" );
    }
}
