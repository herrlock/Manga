package de.herrlock.manga.host;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * A class that has the purpose of creating {@linkplain ChapterList}s. Can be extended to provide an alternative implementation of
 * {@link #getChapterList(DownloadConfiguration)}
 * 
 * @author HerrLock
 */
public class Hoster implements Comparable<Hoster> {

    private final Class<? extends ChapterList> baseClass;
    private final String name;
    private final URL baseUrl;

    /**
     * creates an instance of this Hoster
     * 
     * @param baseClass
     *            the subclass of ChapterList to create instances from with {@linkplain #getChapterList(DownloadConfiguration)}.
     *            The class must be annotated with {@link Details}
     */
    public Hoster( final Class<? extends ChapterList> baseClass ) {
        this.baseClass = Objects.requireNonNull( baseClass );
        Details hosterDetails = Objects.requireNonNull( baseClass.getAnnotation( Details.class ),
            "Implementations of ChapterList require a name and a baseUrl provided by the annotation @Details" );
        this.name = Objects.requireNonNull( hosterDetails.name() );
        try {
            this.baseUrl = new URL( Objects.requireNonNull( hosterDetails.baseUrl() ) );
        } catch ( final MalformedURLException ex ) {
            throw new IllegalStateException( ex );
        }
    }

    /**
     * Creates an instance of the actual ChapterList. Can be overridden if a special behaviour is required. The
     * default-implementation calls the constructor with a {@link DownloadConfiguration} in the signature with the help of the
     * reflection-api (java.lang.reflect)
     * 
     * @param conf
     *            the {@link DownloadConfiguration} to use
     * @return an actual ChapterList-implementation
     * @throws IOException
     *             may be thrown by the called ChapterList's constructor (not required though)
     */
    @SuppressWarnings( "unused" )
    public ChapterList getChapterList( final DownloadConfiguration conf ) throws IOException {
        try {
            Constructor<? extends ChapterList> constructor = this.baseClass.getConstructor( DownloadConfiguration.class );
            return constructor.newInstance( conf );
        } catch ( final NoSuchMethodException ex ) {
            throw new IllegalStateException(
                "The called implementation of ChapterList must contain a constructor accepting a DownloadConfiguration", ex );
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
    public int compareTo( Hoster other ) {
        String thisBaseClassName = this.baseClass.getName();
        String otherBaseClassName = other.baseClass.getName();
        return thisBaseClassName.compareTo( otherBaseClassName );
    }

    @Override
    public boolean equals( Object other ) {
        return other != null && other instanceof Hoster && this.compareTo( ( Hoster ) other ) == 0;
    }

    @Override
    public int hashCode() {
        return this.baseClass.getName().hashCode();
    }
}
