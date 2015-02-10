package de.herrlock.manga.host;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Locale;
import java.util.Map;

import de.herrlock.manga.host.ChapterList.Chapter;
import de.herrlock.manga.util.Utils;

/**
 * A class the consists of multiple Chapters
 * 
 * @author HerrLock
 */
public abstract class ChapterList extends ArrayList<Chapter> {

    /**
     * creates an instance of {@linkplain ChapterList}, gets the right {@linkplain Hoster} from the {@linkplain URL} in
     * {@code Utils.arguments}
     * 
     * @return an instance of {@link ChapterList}; when no suitable Hoster can be detected {@code null}
     * @throws IOException
     *             thrown by {@link Hoster#getChapterList(URL)}
     */
    public static ChapterList getInstance() throws IOException {
        URL url = Utils.getMangaURL();
        Hoster h = Hoster.getHostByURL( url );
        if ( h == null ) {
            throw new IllegalArgumentException( url + " could not be resolved to a registered host." );
        }
        return h.getChapterList( url );
    }

    private final ChapterPattern cp;

    protected ChapterList() {
        String pattern = Utils.getPattern();
        this.cp = ( pattern == null || pattern.equals( "" ) ? null : new ChapterPattern( pattern ) );
    }

    public void addChapter( String number, URL chapterUrl ) {
        if ( this.cp == null || this.cp.contains( number ) )
            super.add( new Chapter( number, chapterUrl ) );
    }

    /**
     * returns the name of the manga. used for the target-folder
     */
    public abstract String getMangaName();

    /**
     * returns the {@link URL} of one page's image
     */
    public abstract URL imgLink( URL url ) throws IOException;

    /**
     * returns all page-{@link URL}s of one chapter
     */
    public abstract Map<Integer, URL> getAllPageURLs( URL url ) throws IOException;

    /**
     * gets all URLs for one Chapter
     */
    public Map<Integer, URL> getAllPageURLs( Chapter c ) throws IOException {
        return getAllPageURLs( c.chapterUrl );
    }

    /**
     * a class to
     * 
     * @author HerrLock
     */
    public static class Chapter {
        final String number;
        final URL chapterUrl;

        Chapter( String number, URL url ) {
            this.number = number;
            this.chapterUrl = url;
        }

        public final String getNumber() {
            return this.number;
        }

        @Override
        public String toString() {
            return MessageFormat.format( "{0}: {1}", this.number, this.chapterUrl );
        }
    }

    private static class ChapterPattern extends ArrayList<String> {
        private static final long serialVersionUID = 1L;

        ChapterPattern( String pattern ) {
            if ( pattern.matches( "([^;]+;?)+" ) ) {
                for ( String s : pattern.split( ";" ) ) {
                    String[] chapter = s.split( "-" );
                    if ( chapter.length == 1 ) {
                        super.add( s );
                    } else if ( chapter.length == 2 ) {
                        int first = Integer.parseInt( chapter[0] );
                        int last = Integer.parseInt( chapter[1] );
                        for ( int i = first; i <= last; i++ )
                            super.add( i + "" );
                    }
                }
            }
        }
    }

    /**
     * all defined Hoster
     * 
     * @author HerrLock
     */
    public static enum Hoster implements Comparator<Hoster> {
        MANGAPANDA( "Mangapanda", "http://www.mangapanda.com/" ), //
        PUREMANGA( "PureManga", "http://www.pure-manga.org/" ), //
        MANGAFOX( "Mangafox", "http://www.mangafox.me/" ), //
        ;

        private final String name;
        private final URL url;

        /**
         * 
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

        public String getName() {
            return this.name;
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
                    return new Panda( mangaUrl );
                case PUREMANGA:
                    return new PureManga( mangaUrl );
                case MANGAFOX:
                    return new Fox( mangaUrl );
                default:
                    throw new RuntimeException( "Hoster \"" + this + "\" not found" );
            }
        }

        @Override
        public int compare( Hoster o1, Hoster o2 ) {
            String o1Lower = o1.getName().toLowerCase( Locale.GERMAN );
            String o2Lower = o2.getName().toLowerCase( Locale.GERMAN );
            return o1Lower.compareTo( o2Lower );
        }

        public static Hoster getHostByURL( URL url ) {
            String givenUrlHost = url.getHost();
            if ( givenUrlHost.matches( "www\\..+" ) ) {
                givenUrlHost = givenUrlHost.substring( 4 );
            }
            for ( Hoster h : Hoster.values() ) {
                String hostUrlHost = h.getURL().getHost();
                if ( hostUrlHost.matches( "www\\..+" ) ) {
                    hostUrlHost = hostUrlHost.substring( 4 );
                }
                if ( hostUrlHost.equalsIgnoreCase( givenUrlHost ) ) {
                    return h;
                }
            }
            return null;
        }

    }

}
