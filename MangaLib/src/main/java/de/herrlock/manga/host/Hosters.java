package de.herrlock.manga.host;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.host.impl.MangaFox;
import de.herrlock.manga.host.impl.MangaPanda;
import de.herrlock.manga.host.impl.PureManga;

/**
 * A Utility-class containing the predefined {@link Hoster} as well as those loaded at runtime
 * 
 * @author HerrLock
 */
public final class Hosters {
    private static final Logger logger = LogManager.getLogger();

    private static final Collection<Hoster> hosters = new HashSet<>();

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
        public int compare( final Hoster h1, final Hoster h2 ) {
            String h1LowerName = h1.getName().toLowerCase( Locale.GERMAN );
            String h2LowerName = h2.getName().toLowerCase( Locale.GERMAN );
            return h1LowerName.compareTo( h2LowerName );
        }
    };

    static {
        for ( Class<? extends ChapterList> c : Arrays.asList( MangaPanda.class, MangaFox.class, PureManga.class ) ) {
            registerHoster( c );
        }

        // TODO check if this works, write JUnit-tests
        List<String> classes = new ArrayList<>();
        try {
            List<String> allLines = Files.readAllLines( Paths.get( ".", "additionalHoster.txt" ), StandardCharsets.UTF_8 );
            classes.addAll( allLines );
        } catch ( final NoSuchFileException ex ) {
            logger.info( "additionalHoster.txt not found, ignoring" );
        } catch ( final IOException ex ) {
            throw new MDRuntimeException( "could not load additionalHoster.txt", ex );
        }
        for ( String s : classes ) {
            try {
                Class<?> c = Class.forName( s );
                if ( ChapterList.class.isAssignableFrom( c ) ) {
                    registerHoster( c.asSubclass( ChapterList.class ) );
                } else {
                    logger.warn( "Class {} is no subclass of ChapterList", c );
                }
            } catch ( final ClassNotFoundException ex ) {
                logger.warn( "Could not find class {}", s );
            }
        }
    }

    /**
     * adds a {@link Hoster} to the global pool
     * 
     * @param hoster
     *            the Hoster to add
     */
    public static void registerHoster( final Hoster hoster ) {
        hosters.add( hoster );
    }

    /**
     * adds a {@link Hoster} to the global pool
     * 
     * @param c
     *            the class to create a Hoster with
     * @see Hoster#Hoster(Class)
     */
    public static void registerHoster( final Class<? extends ChapterList> c ) {
        registerHoster( new Hoster( c ) );
    }

    /**
     * checks all Hoster for the one that matches the given URL
     * 
     * @param url
     *            the URL to check the Hoster against
     * @return the Hoster that has the given URL; when none is found {@code null}
     */
    public static Hoster getHostByURL( final URL url ) {
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

    /**
     * returns an {@linkplain Collections#unmodifiableList(List) unmodifiable List} containing the registered {@link Hoster}
     * 
     * @return the registered Hoster
     */
    public static List<Hoster> values() {
        return Collections.unmodifiableList( new ArrayList<>( hosters ) );
    }

    /**
     * returns a sorted, {@linkplain Collections#unmodifiableList(List) unmodifiable List} containing the registered
     * {@link Hoster}. The {@link Comparator} used for sorting is {@link Hosters#NAME_COMPARATOR}
     * 
     * @return the registered Hoster
     */
    public static List<Hoster> sortedValues() {
        return sortedValues( NAME_COMPARATOR );
    }

    /**
     * returns a sorted, {@linkplain Collections#unmodifiableList(List) unmodifiable List} containing the registered
     * {@link Hoster}.
     * 
     * @param comparator
     *            the comparator to sort with
     * @return the registered Hoster
     */
    public static List<Hoster> sortedValues( final Comparator<Hoster> comparator ) {
        ArrayList<Hoster> hosterCopy = new ArrayList<>( hosters );
        Collections.sort( hosterCopy, comparator );
        return Collections.unmodifiableList( hosterCopy );
    }

    private Hosters() {
        // avoid instantiation
    }
}
