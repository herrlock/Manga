package de.herrlock.manga.host;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import java.util.regex.Pattern;

import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * all defined Hoster
 * 
 * @author HerrLock
 */
// TODO refactor to implement an Interface to enable registering additional Hoster
public enum ProvidedHoster implements Hoster {
    /**
     * Hoster Mangapanda with the URL mangapanda.com
     */
    MANGAPANDA( "Mangapanda", "http://www.mangapanda.com/" ) {
        @Override
        public ChapterList getChapterList( DownloadConfiguration conf ) throws IOException {
            return new MangaPanda( conf );
        }
    },
    /**
     * Hoster Pure-Manga with the URL pure-manga.org
     */
    PUREMANGA( "Pure-Manga", "http://www.pure-manga.org/" ) {
        @Override
        public ChapterList getChapterList( DownloadConfiguration conf ) throws IOException {
            return new PureManga( conf );
        }
    },
    /**
     * Hoster Mangafox with the URL mangafox.me
     */
    MANGAFOX( "Mangafox", "http://www.mangafox.me/" ) {
        @Override
        public ChapterList getChapterList( DownloadConfiguration conf ) throws IOException {
            return new MangaFox( conf );
        }
    };

    private final String name;
    private final URL url;

    /**
     * @param name
     *            the Hoster's name
     * @param url
     *            the Hoster's "main"-URL
     */
    private ProvidedHoster( String name, String url ) {
        this.name = name;
        try {
            this.url = new URL( url );
        } catch ( MalformedURLException ex ) {
            throw new RuntimeException( ex );
        }
    }

    @Override
    public URL getUrl() {
        return this.url;
    }

    @Override
    public String getName() {
        return this.name;
    }

    /**
     * checks all Hoster for the one that matches the given URL
     * 
     * @param url
     *            the URL to check the Hoster against
     * @return the Hoster that has the given URL; when none is found {@code null}
     */
    public static Hoster getHostByURL( URL url ) {
        Pattern www = Pattern.compile( "www\\..+" );
        String givenUrlHost = url.getHost();
        if ( www.matcher( givenUrlHost ).matches() ) {
            givenUrlHost = givenUrlHost.substring( 4 );
        }
        for ( ProvidedHoster h : ProvidedHoster.values() ) {
            String hostUrlHost = h.url.getHost();
            if ( www.matcher( hostUrlHost ).matches() ) {
                hostUrlHost = hostUrlHost.substring( 4 );
            }
            if ( hostUrlHost.equalsIgnoreCase( givenUrlHost ) ) {
                return h;
            }
        }
        return null;
    }

    @Override
    public String toString() {
        return this.name() + "\t" + this.url;
    }

    /**
     * sorts the result of {@link #values()} with the {@link #NAME_COMPARATOR}
     * 
     * @return a sorted array of all Hosters
     */
    public static Hoster[] sortedValues() {
        ProvidedHoster[] values = ProvidedHoster.values();
        ProvidedHoster[] copy = Arrays.copyOf( values, values.length );
        Arrays.sort( copy, NAME_COMPARATOR );
        return copy;
    }

    /**
     * A {@link Comparator} to compare to {@linkplain ProvidedHoster} with their {@linkplain #getName() name}
     */
    public static final Comparator<ProvidedHoster> NAME_COMPARATOR = new Comparator<ProvidedHoster>() {
        /**
         * compares the hoster by their name
         * 
         * @param h1
         *            the first Hoster
         * @param h2
         *            the second Hoster
         */
        @Override
        public int compare( ProvidedHoster h1, ProvidedHoster h2 ) {
            String o1Lower = h1.getName().toLowerCase( Locale.GERMAN );
            String o2Lower = h2.getName().toLowerCase( Locale.GERMAN );
            return o1Lower.compareTo( o2Lower );
        }
    };

}
