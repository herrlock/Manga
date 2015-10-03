package de.herrlock.manga.host;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * a class that has the purpose of creating {@linkplain ChapterList}s
 * 
 * @author HerrLock
 */
public class Hoster {

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
    public Hoster( Class<? extends ChapterList> baseClass ) {
        this.baseClass = baseClass;
        Details hosterDetails = Objects.requireNonNull( baseClass.getAnnotation( Details.class ),
            "Implementations of ChapterList require a name and a URL provided by the annotation @Hoster" );
        this.name = hosterDetails.name();
        try {
            this.baseUrl = new URL( hosterDetails.baseUrl() );
        } catch ( MalformedURLException ex ) {
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
    public ChapterList getChapterList( DownloadConfiguration conf ) throws IOException {
        try {
            Constructor<? extends ChapterList> constructor = this.baseClass.getConstructor( DownloadConfiguration.class );
            return constructor.newInstance( conf );
        } catch ( NoSuchMethodException ex ) {
            throw new IllegalStateException(
                "The called implementation of ChapterList must contain a constructor accepting a DownloadConfiguration", ex );
        } catch ( InstantiationException ex ) {
            throw new IllegalStateException( "The called class is abstract", ex );
        } catch ( IllegalAccessException ex ) {
            throw new RuntimeException( "The called constructor is not accessible", ex );
        } catch ( IllegalArgumentException ex ) {
            throw new RuntimeException( "The called constructor does not take one DownloadConfiguration as parameter", ex );
        } catch ( InvocationTargetException ex ) {
            throw new RuntimeException( "The called constructor threw an exception", ex.getCause() );
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

}
