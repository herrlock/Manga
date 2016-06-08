package de.herrlock.manga.host;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.Iterables;

import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.util.Lookup;

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
         * Compares the hoster by their name. Uses {@link Locale#GERMAN} to convert the names to lowercase.
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
        Collection<? extends Class<ChapterList>> lookupAll = Lookup.lookupAll( ChapterList.class );
        for ( Class<? extends ChapterList> c : lookupAll ) {
            registerHoster( c );
        }

        Path additionalHostersPath = Paths.get( ".", "additionalHoster.txt" );
        if ( Files.exists( additionalHostersPath ) ) {
            try {
                List<String> allLines = Files.readAllLines( additionalHostersPath, StandardCharsets.UTF_8 );
                loadAdditionalHosters( allLines );
            } catch ( final NoSuchFileException ex ) {
                logger.info( "additionalHoster.txt not found, ignoring" );
            } catch ( final IOException ex ) {
                throw new MDRuntimeException( "could not load additionalHoster.txt", ex );
            }
        }
    }

    private static void loadAdditionalHosters( final List<String> lines ) {
        logger.traceEntry( "lines: {}", lines );

        final Predicate<String> isBracketLine = new Predicate<String>() {
            @Override
            public boolean apply( final String input ) {
                return input != null && input.startsWith( "[" ) && input.endsWith( "]" );
            }
        };

        Iterable<String> resourcesPathLines = Iterables.filter( lines, isBracketLine );
        List<URL> resourcePaths = loadResourcePaths( resourcesPathLines );

        URL[] resourcePathArray = resourcePaths.toArray( new URL[resourcePaths.size()] );
        try ( URLClassLoader classLoader = new URLClassLoader( resourcePathArray, Hosters.class.getClassLoader() ) ) {
            Iterable<String> otherLines = Iterables.filter( lines, Predicates.not( isBracketLine ) );
            for ( String line : otherLines ) {
                if ( !line.trim().isEmpty() && !line.startsWith( "#" ) ) {
                    loadExtraClass( classLoader, line );
                }
            }
        } catch ( IOException ex ) {
            throw new MDRuntimeException( ex );
        }
    }

    private static List<URL> loadResourcePaths( final Iterable<String> lines ) {
        List<URL> resourcePaths = new ArrayList<>();
        try {
            for ( String line : lines ) {
                String filename = line.substring( 1, line.length() - 1 );
                File nextFile = new File( filename );
                if ( nextFile.exists() ) {
                    URL nextFileURL = nextFile.toURI().toURL();
                    resourcePaths.add( nextFileURL );
                } else {
                    logger.warn( "The File \"{}\" does not exist", nextFile );
                }
            }
        } catch ( MalformedURLException ex ) {
            throw new MDRuntimeException( ex );
        }
        return resourcePaths;
    }

    private static void loadExtraClass( final URLClassLoader classLoader, final String line ) {
        try {
            Class<?> c = Class.forName( line, false, classLoader );
            if ( ChapterList.class.isAssignableFrom( c ) ) {
                Class<? extends ChapterList> asSubclass = c.asSubclass( ChapterList.class );
                boolean added = registerHoster( asSubclass );
                if ( added ) {
                    logger.info( "Class {} registered", c );
                } else {
                    logger.info( "Class {} already registered", c );
                }
            } else {
                logger.warn( "Class {} is no subclass of ChapterList", c );
            }
        } catch ( final ClassNotFoundException ex ) {
            logger.warn( "Could not find class {}", line );
        }
    }

    /**
     * adds a {@link Hoster} to the global pool
     * 
     * @param hoster
     *            the Hoster to add
     * @return if the Hoster was actually added
     * @see java.util.Collection#add(Object)
     */
    public static boolean registerHoster( final Hoster hoster ) {
        logger.debug( "add Hoster: {}", hoster );
        return hosters.add( hoster );
    }

    /**
     * adds a {@link Hoster} to the global pool
     * 
     * @param c
     *            the class to create a Hoster with
     * @return if the Hoster was actually added
     * @see Hoster#Hoster(Class)
     */
    public static boolean registerHoster( final Class<? extends ChapterList> c ) {
        return registerHoster( new Hoster( c ) );
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
