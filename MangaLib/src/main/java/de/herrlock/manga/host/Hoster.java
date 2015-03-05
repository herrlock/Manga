package de.herrlock.manga.host;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Comparator;
import java.util.Locale;

/**
 * all defined Hoster
 * 
 * @author HerrLock
 */
public enum Hoster implements Comparator<Hoster> {
    MANGAPANDA( "Mangapanda", "http://www.mangapanda.com/" ), //
    PUREMANGA( "PureManga", "http://www.pure-manga.org/" ), //
    MANGAFOX( "Mangafox", "http://www.mangafox.me/" ), //
    ;

    private final String name;
    private final URL url;

    /**
     * @param name
     *            the Hoster's name
     * @param url
     *            the Hoster's "main"-URL
     */
    Hoster( String name, String url ) {
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

    /**
     * creates an instance of a Host, specified by the URL's host-part
     * 
     * @param mangaUrl
     *            the URL to the mainpage
     * @return an instance of the ChapterList specified by the current {@link Hoster}-Object
     * @throws IOException
     *             thrown by the constructors of the special ChapterList-implementations
     */
    public ChapterList getChapterList( URL mangaUrl ) throws IOException {
        switch ( this ) {
            case MANGAPANDA:
                return new MangaPanda( mangaUrl );
            case PUREMANGA:
                return new PureManga( mangaUrl );
            case MANGAFOX:
                return new MangaFox( mangaUrl );
            default:
                throw new RuntimeException( "Hoster \"" + this + "\" not found" );
        }
    }

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
        String o1Lower = h1.name.toLowerCase( Locale.GERMAN );
        String o2Lower = h2.name.toLowerCase( Locale.GERMAN );
        return o1Lower.compareTo( o2Lower );
    }

    /**
     * checks all Hoster for the one that matches the given URL
     * 
     * @param url
     *            the URL to check the Hoster against
     * @return the Hoster that has the given URL; when none is found {@code null}
     */
    public static Hoster getHostByURL( URL url ) {
        String givenUrlHost = url.getHost();
        if ( givenUrlHost.matches( "www\\..+" ) ) {
            givenUrlHost = givenUrlHost.substring( 4 );
        }
        for ( Hoster h : Hoster.values() ) {
            String hostUrlHost = h.url.getHost();
            if ( hostUrlHost.matches( "www\\..+" ) ) {
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
}
