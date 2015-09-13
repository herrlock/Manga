package de.herrlock.manga.downloader.pmc;

import java.util.AbstractMap.SimpleImmutableEntry;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * An Iterable of {@link SimpleImmutableEntry}. The entries are stored in a {@link List}
 * 
 * @param <K>
 *            the type of the key
 * @param <V>
 *            the type of the value
 * 
 * @author HerrLock
 */
public class EntryList<K, V> implements Iterable<SimpleImmutableEntry<K, V>> {
    private final List<SimpleImmutableEntry<K, V>> entries;

    /**
     * Creates a new empty EntryList
     * 
     * @see ArrayList#ArrayList()
     */
    public EntryList() {
        this.entries = new ArrayList<>();
    }

    /**
     * Creates a new EntryList with the given default capacity
     * 
     * @param initialCapacity
     *            The initially size of the list
     * 
     * @see ArrayList#ArrayList(int)
     */
    public EntryList( int initialCapacity ) {
        this.entries = new ArrayList<>( initialCapacity );
    }

    /**
     * Creates a new empty EntryList as a copy of the given Collection of Entries
     * 
     * @param c
     *            the collection to copy the elements from
     * 
     * @see ArrayList#ArrayList(Collection)
     */
    public EntryList( Collection<? extends SimpleImmutableEntry<K, V>> c ) {
        this.entries = new ArrayList<>( c );
    }

    /**
     * Creates a new empty EntryList as a copy of the given EntryList
     * 
     * @param entryList
     *            the entryList to copy the elements from
     * 
     * @see ArrayList#ArrayList(Collection)
     */
    public EntryList( EntryList<K, V> entryList ) {
        this( entryList.entries );
    }

    /**
     * Adds a single Entry to the list
     * 
     * @param key
     *            the key of the new entry
     * @param value
     *            the value of the new entry
     */
    public void addEntry( K key, V value ) {
        this.entries.add( new SimpleImmutableEntry<>( key, value ) );
    }

    /**
     * 
     * @return the current number of elements
     */
    public int size() {
        return this.entries.size();
    }

    /**
     * Sorts the entries with the given {@link Comparator}
     * 
     * @param c
     *            the {@link Comparator} to use
     */
    public void sort( Comparator<? super SimpleImmutableEntry<K, V>> c ) {
        Collections.sort( this.entries, c );
    }

    /**
     * 
     */
    @Override
    public Iterator<SimpleImmutableEntry<K, V>> iterator() {
        return this.entries.iterator();
    }

    /**
     * Returns a new {@link Comparator} that uses {@link Comparator#compare(Object, Object)} of the given Comparator to compare
     * the keys of the entries
     * 
     * @param comparator
     *            the base-comparator
     * @return a new comparator
     */
    public Comparator<SimpleImmutableEntry<String, ?>> getStringComparator( final Comparator<String> comparator ) {
        return new Comparator<SimpleImmutableEntry<String, ?>>() {
            @Override
            public int compare( SimpleImmutableEntry<String, ?> o1, SimpleImmutableEntry<String, ?> o2 ) {
                return comparator.compare( o1.getKey(), o2.getKey() );
            }
        };
    }

}
