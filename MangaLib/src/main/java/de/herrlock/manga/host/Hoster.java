package de.herrlock.manga.host;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;

import de.herrlock.manga.exceptions.HosterInstantiationException;
import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * A class that has the purpose of creating {@linkplain ChapterList}s. Can be extended to provide an alternative implementation of
 * {@link #getChapterList(DownloadConfiguration)}
 * 
 * @author HerrLock
 */
public final class Hoster implements Comparable<Hoster> {

    private final InstantiationProxy proxy;
    private final String baseClassName;
    private final String name;
    private final URL baseUrl;

    /**
     * Creates an instance of this Hoster
     * 
     * @param proxy
     *            An implementation of {@link InstantiationProxy} that returns an object of the {@link ChapterList}-implementation
     */
    public Hoster( final InstantiationProxy proxy ) throws HosterInstantiationException {
        this.proxy = requireNonNull( proxy, "InstantiationProxy is null" );
        Class<? extends ChapterList> baseClass = requireNonNull( this.proxy.getProxiedClass(),
            "The InstantiationProxy must return the proxied class." );
        this.baseClassName = baseClass.getName();
        Details hosterDetails = requireNonNull( baseClass.getAnnotation( Details.class ),
            "Implementations of ChapterList require a name and a baseUrl provided by the annotation @Details" );
        this.name = requireNonNull( hosterDetails.name(), "@Details.name() is null." );
        try {
            String urlString = hosterDetails.baseUrl();
            this.baseUrl = new URL( requireNonNull( urlString, "@Details.baseUrl() is null." ) );
        } catch ( final MalformedURLException ex ) {
            throw new HosterInstantiationException( ex );
        }
    }

    public static <K> K requireNonNull( final K k, final String message ) throws HosterInstantiationException {
        if ( k == null ) {
            throw new HosterInstantiationException( message );
        }
        return k;
    }

    public <T extends ChapterList> Hoster( final Class<T> clazz ) throws HosterInstantiationException {
        this( new GeneralInstantiationProxy( clazz ) );
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
            return this.proxy.getInstance( conf );
        } catch ( IOException ex ) {
            throw new MDRuntimeException( ex );
        }
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
        String thisBaseClassName = this.baseClassName;
        String otherBaseClassName = other.baseClassName;
        return thisBaseClassName.compareTo( otherBaseClassName );
    }

    @Override
    public boolean equals( final Object other ) {
        return other != null && other instanceof Hoster && this.compareTo( ( Hoster ) other ) == 0;
    }

    @Override
    public int hashCode() {
        return this.baseClassName.hashCode();
    }

    @Override
    public String toString() {
        return MessageFormat.format( "Hoster (name: {0}, url: {1})", this.name, this.baseUrl );
    }

    /**
     * @author HerrLock
     */
    public static final class GeneralInstantiationProxy extends InstantiationProxy {
        private final Class<? extends ChapterList> clazz;

        GeneralInstantiationProxy( final Class<? extends ChapterList> clazz ) {
            this.clazz = clazz;
        }

        @Override
        public Class<? extends ChapterList> getProxiedClass() {
            return this.clazz;
        }

        @Override
        public ChapterList getInstance( final DownloadConfiguration conf ) throws IOException {
            Constructor<? extends ChapterList> constructor;
            try {
                constructor = this.clazz.getConstructor( DownloadConfiguration.class );
            } catch ( final NoSuchMethodException ex ) {
                String message = "The called implementation of ChapterList must contain a constructor accepting a DownloadConfiguration";
                throw new IllegalStateException( message, ex );
            }
            try {
                return constructor.newInstance( conf );
            } catch ( final InstantiationException ex ) {
                throw new IllegalStateException( "The called class is abstract", ex );
            } catch ( final IllegalAccessException ex ) {
                throw new MDRuntimeException( "The called constructor is not accessible", ex );
            } catch ( final IllegalArgumentException ex ) {
                throw new MDRuntimeException( "The called constructor does not take one DownloadConfiguration as parameter", ex );
            } catch ( final InvocationTargetException ex ) {
                throw new MDRuntimeException( "The called constructor threw an exception", ex );
            }
        }
    }
}
