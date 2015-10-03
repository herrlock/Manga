package de.herrlock.manga.host;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.host.impl.MangaFox;
import de.herrlock.manga.host.impl.MangaPanda;
import de.herrlock.manga.host.impl.PureManga;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * all defined Hoster
 * 
 * @author HerrLock
 */
public final class Hosters {
    private static final Logger logger = LogManager.getLogger();

    private static final List<Hoster> hosters = new ArrayList<>( Arrays.asList( new Hoster( MangaPanda.class) {
        @Override
        public ChapterList getChapterList( DownloadConfiguration conf ) throws IOException {
            return new MangaPanda( conf );
        }
    }, new Hoster( MangaFox.class) {
        @Override
        public ChapterList getChapterList( DownloadConfiguration conf ) throws IOException {
            return new MangaFox( conf );
        }
    }, new Hoster( PureManga.class) {
        @Override
        public ChapterList getChapterList( DownloadConfiguration conf ) throws IOException {
            return new PureManga( conf );
        }
    } ) );

    /**
     * A {@link Comparator} to compare two {@link Hoster} according to their name. Uses {@link String#compareTo(String)}
     */
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

    static {
        // TODO check if this works, write JUnit-tests
        List<String> classes = new ArrayList<>();
        try {
            List<String> allLines = Files.readAllLines( Paths.get( ".", "additionalHoster.txt" ), StandardCharsets.UTF_8 );
            classes.addAll( allLines );
        } catch ( NoSuchFileException ex ) {
            logger.warn( "additionalHoster.txt not found, ignoring" );
        } catch ( IOException ex ) {
            throw new RuntimeException( "could not load additionalHoster.txt", ex );
        }
        for ( String s : classes ) {
            try {
                Class<?> c = Class.forName( s );
                if ( ChapterList.class.isAssignableFrom( c ) ) {
                    Class<? extends ChapterList> cl = c.asSubclass( ChapterList.class );
                    try {
                        final Constructor<? extends ChapterList> constructor = cl.getConstructor( DownloadConfiguration.class );
                        hosters.add( new Hoster( cl) {
                            @Override
                            public ChapterList getChapterList( DownloadConfiguration conf ) throws IOException {
                                try {
                                    return constructor.newInstance( conf );
                                } catch ( ReflectiveOperationException ex ) {
                                    throw new RuntimeException( ex );
                                }
                            }
                        } );
                    } catch ( NoSuchMethodException ex ) {
                        logger.warn( "The class {} does not contain a constructor acepting a DownloadConfiguration", c );
                    }

                }
            } catch ( ClassNotFoundException ex ) {
                logger.warn( "Could not find class {}", s );
            }
        }
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
        for ( Hoster h : hosters ) {
            String hostUrlHost = h.getBaseUrl().getHost();
            if ( www.matcher( hostUrlHost ).matches() ) {
                hostUrlHost = hostUrlHost.substring( 4 );
            }
            if ( hostUrlHost.equalsIgnoreCase( givenUrlHost ) ) {
                return h;
            }
        }
        return null;
    }

    public static List<Hoster> values() {
        return Collections.unmodifiableList( new ArrayList<>( hosters ) );
    }

    public static List<Hoster> sortedValues() {
        return sortedValues( NAME_COMPARATOR );
    }

    public static List<Hoster> sortedValues( Comparator<Hoster> comparator ) {
        ArrayList<Hoster> hosterCopy = new ArrayList<>( hosters );
        Collections.sort( hosterCopy, comparator );
        return Collections.unmodifiableList( hosterCopy );
    }

    private Hosters() {
        // avoid instantiation
    }
}
