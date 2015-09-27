package de.herrlock.manga.host;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.Locale;
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

    public String getName() {
        return this.name;
    }

    public URL getBaseUrl() {
        return this.baseUrl;
    }

    public static final Comparator<Hoster> NAME_COMPARATOR = new Comparator<Hoster>() {
        /**
         * compares the hoster by their name
         * 
         * @param h1
         *            the first Hoster
         * @param h2
         *            the second Hoster
         */
        @Override
        public int compare( Hoster h1, Hoster h2 ) {
            String h1LowerName = h1.getName().toLowerCase( Locale.GERMAN );
            String h2LowerName = h2.getName().toLowerCase( Locale.GERMAN );
            return h1LowerName.compareTo( h2LowerName );
        }
    };

}
