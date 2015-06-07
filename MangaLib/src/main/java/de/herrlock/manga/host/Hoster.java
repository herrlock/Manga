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
public enum Hoster {
    MANGAPANDA( "Mangapanda", "http://www.mangapanda.com/" ) {
        @Override
        public ChapterList getChapterList( URL mangaUrl, DownloadConfiguration conf ) throws IOException {
            return new MangaPanda( mangaUrl, conf );
        }
    },
    PUREMANGA( "Pure-Manga", "http://www.pure-manga.org/" ) {
        @Override
        public ChapterList getChapterList( URL mangaUrl, DownloadConfiguration conf ) throws IOException {
            return new PureManga( mangaUrl, conf );
        }
    },
    MANGAFOX( "Mangafox", "http://www.mangafox.me/" ) {
        @Override
        public ChapterList getChapterList( URL mangaUrl, DownloadConfiguration conf ) throws IOException {
            return new MangaFox( mangaUrl, conf );
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
    private Hoster( String name, String url ) {
        this.name = name;
        try {
            this.url = new URL( url );
        } catch ( MalformedURLException ex ) {
            throw new RuntimeException( ex );
        }
    }

    public URL getURL() {
        return this.url;
    }

    public String getName() {
        return this.name;
    }

    /**
     * creates an instance of a Host, specified by the URL's host-part
     * 
     * @param mangaUrl
     *            the URL to the mainpage
     * @return an instance of the ChapterList specified by the current {@link Hoster}-Object
     * @throws IOException
     *             thrown by the constructors of the special ChapterList-implementations
     */
    public abstract ChapterList getChapterList( URL mangaUrl, DownloadConfiguration conf ) throws IOException;

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
        for ( Hoster h : Hoster.values() ) {
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

    public static Hoster[] sortedValues() {
        Hoster[] values = Hoster.values();
        Hoster[] copy = Arrays.copyOf( values, values.length );
        Arrays.sort( copy, NAME_COMPARATOR );
        return copy;
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
            String o1Lower = h1.getName().toLowerCase( Locale.GERMAN );
            String o2Lower = h2.getName().toLowerCase( Locale.GERMAN );
            return o1Lower.compareTo( o2Lower );
        }
    };

}
