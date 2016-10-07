package de.herrlock.manga.host;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.Collection;
import java.util.Comparator;
import java.util.Locale;

import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.host.annotations.Details;
import de.herrlock.manga.host.exceptions.HosterInstantiationException;
import de.herrlock.manga.index.HosterListEntry;
import de.herrlock.manga.util.configuration.DownloadConfiguration;
import de.herrlock.manga.util.configuration.IndexerConfiguration;

/**
 * A class that has the purpose of creating {@linkplain ChapterList}s. Can be extended to provide an alternative implementation of
 * {@link #getChapterList(DownloadConfiguration)}
 * 
 * @author HerrLock
 */
public final class Hoster implements Comparable<Hoster> {

    private final HosterImpl hosterImpl;
    private final String name;
    private final URL baseUrl;

    /**
     * Creates an instance of this Hoster
     * 
     * @param hosterImpl
     *            An implementation of {@link HosterImpl}.
     */
    public Hoster( final HosterImpl hosterImpl ) throws HosterInstantiationException {
        this.hosterImpl = requireNonNull( hosterImpl, "HosterImpl is null" );
        Details hosterDetails = requireNonNull( this.hosterImpl.getDetails(),
            "Implementations of HosterImpl require a name and a baseUrl provided by the annotation @Details" );
        this.name = requireNonNull( hosterDetails.name(), "@Details.name() is null." );
        try {
            String urlString = requireNonNull( hosterDetails.baseUrl(), "@Details.baseUrl() is null." );
            this.baseUrl = new URL( urlString );
        } catch ( final MalformedURLException ex ) {
            throw new HosterInstantiationException( ex );
        }
    }

    public Hoster( final Class<? extends HosterImpl> clazz ) throws HosterInstantiationException {
        this( new HosterImplWrapper( clazz ) );
    }

    public static <K> K requireNonNull( final K k, final String message ) throws HosterInstantiationException {
        if ( k == null ) {
            throw new HosterInstantiationException( message );
        }
        return k;
    }

    /**
     * Creates an instance of the actual ChapterList. Can be overridden if a special behaviour is required. The
     * default-implementation calls the constructor with a {@link DownloadConfiguration} in the signature with the help of the
     * reflection-api (java.lang.reflect)
     * 
     * @param conf
     *            the {@link DownloadConfiguration} to use
     * @return an actual ChapterList-implementation
     */
    public ChapterList getChapterList( final DownloadConfiguration conf ) {
        try {
            return this.hosterImpl.getChapterList( conf );
        } catch ( IOException ex ) {
            throw new MDRuntimeException( ex );
        }
    }

    public Collection<HosterListEntry> getAvailabile( final IndexerConfiguration conf ) {
        return this.hosterImpl.getAvailabile( conf );
    }

    /**
     * getter for the Hoster's name
     * 
     * @return the Hoster's name
     */
    public String getName() {
        return this.name;
    }

    /**
     * getter for the Hoster's url
     * 
     * @return the Hoster's url
     */
    public URL getBaseUrl() {
        return this.baseUrl;
    }

    @Override
    public int compareTo( final Hoster other ) {
        String thisName = this.name;
        String otherName = other.name;
        return thisName.compareTo( otherName );
    }

    @Override
    public boolean equals( final Object other ) {
        return other != null && other instanceof Hoster && this.compareTo( ( Hoster ) other ) == 0;
    }

    @Override
    public int hashCode() {
        return this.name.hashCode();
    }

    @Override
    public String toString() {
        return MessageFormat.format( "Hoster (name: {0}, url: {1})", this.name, this.baseUrl );
    }

    /**
     * A {@link Comparator} to compare two {@link Hoster} according to their name. Uses {@link String#compareTo(String)}
     */
    public static final Comparator<Hoster> NAME_COMPARATOR = new Comparator<Hoster>() {
        /**
         * Compares the hoster by their name. Uses {@link Locale#GERMANY} to convert the names to lowercase.
         * 
         * @param h1
         *            the first Hoster
         * @param h2
         *            the second Hoster
         */
        @Override
        public int compare( final Hoster h1, final Hoster h2 ) {
            String h1LowerName = h1.getName().toLowerCase( Locale.GERMANY );
            String h2LowerName = h2.getName().toLowerCase( Locale.GERMANY );
            return h1LowerName.compareTo( h2LowerName );
        }
    };

    private static class HosterImplWrapper extends HosterImpl {

        private final HosterImpl actualHosterImpl;

        HosterImplWrapper( final Class<? extends HosterImpl> clazz ) throws HosterInstantiationException {
            try {
                this.actualHosterImpl = clazz.newInstance();
            } catch ( InstantiationException | IllegalAccessException ex ) {
                throw new HosterInstantiationException( ex );
            }
        }

        @Override
        public ChapterList getChapterList( final DownloadConfiguration conf ) throws IOException {
            return this.actualHosterImpl.getChapterList( conf );
        }

        @Override
        public Collection<HosterListEntry> getAvailabile( final IndexerConfiguration conf ) {
            return this.actualHosterImpl.getAvailabile( conf );
        }

        @Override
        public Details getDetails() {
            return this.actualHosterImpl.getDetails();
        }

    }

}
