package de.herrlock.manga.host;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Objects;

import de.herrlock.manga.util.configuration.DownloadConfiguration;

public abstract class Hoster {

    private final String name;
    private final URL baseUrl;

    public Hoster( Class<? extends ChapterList> baseClass ) {
        Details hosterDetails = Objects.requireNonNull( baseClass.getAnnotation( Details.class ),
            "Implementations of ChapterList require a name and a URL provided by the annotation @Hoster" );
        this.name = hosterDetails.name();
        try {
            this.baseUrl = new URL( hosterDetails.baseUrl() );
        } catch ( MalformedURLException ex ) {
            throw new IllegalStateException( ex );
        }
    }

    public abstract ChapterList getChapterList( DownloadConfiguration conf ) throws IOException;

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
