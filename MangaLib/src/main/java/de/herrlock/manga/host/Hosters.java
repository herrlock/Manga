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
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.ServiceLoader;
import java.util.Set;
import java.util.SortedSet;
import java.util.regex.Pattern;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ImmutableSortedSet;
import com.google.common.collect.Iterables;

import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.host.exceptions.HosterInstantiationException;
import de.herrlock.manga.host.exceptions.NoHosterFoundException;

/**
 * A Utility-class containing the predefined {@link Hoster} as well as those loaded at runtime
 * 
 * @author HerrLock
 */
public final class Hosters {

    private static final Logger logger = LogManager.getLogger();

    private static final Collection<Hoster> hosters = new HashSet<>();

    static {
        ServiceLoader<HosterImpl> loader = ServiceLoader.load( HosterImpl.class );
        for ( HosterImpl impl : loader ) {
            registerHoster( impl );
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

    private static void loadAdditionalHosters( final Collection<String> lines ) {
        logger.traceEntry( "lines: {}", lines );

        final Predicate<String> isBracketLine = new Predicate<String>() {
            @Override
            public boolean apply( final String input ) {
                return input != null && input.startsWith( "[" ) && input.endsWith( "]" );
            }
        };

        Iterable<String> resourcesPathLines = Iterables.filter( lines, isBracketLine );
        Collection<URL> resourcePaths = loadResourcePaths( resourcesPathLines );

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

    private static Collection<URL> loadResourcePaths( final Iterable<String> lines ) {
        Queue<URL> resourcePaths = new ArrayDeque<>();
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
            if ( HosterImpl.class.isAssignableFrom( c ) ) {
                Class<? extends HosterImpl> asSubclass = c.asSubclass( HosterImpl.class );
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
        boolean added = hosters.add( hoster );
        logger.debug( "add Hoster: {}, wasAdded: {}", hoster, added );
        return added;
    }

    /**
     * adds a {@link Hoster} to the global pool
     * 
     * @param impl
     *            the {@link HosterImpl} to create a Hoster with
     * @return if the Hoster was actually added
     * @see Hoster#Hoster(HosterImpl)
     */
    public static boolean registerHoster( final HosterImpl impl ) {
        try {
            Hoster hoster = new Hoster( impl );
            return registerHoster( hoster );
        } catch ( HosterInstantiationException ex ) {
            logger.warn( "Hoster could not be instantiated." );
            logger.catching( ex );
            return false;
        }
    }

    public static boolean registerHoster( final Class<? extends HosterImpl> clazz ) {
        try {
            Hoster hoster = new Hoster( clazz );
            return registerHoster( hoster );
        } catch ( HosterInstantiationException ex ) {
            logger.warn( "Hoster could not be instantiated." );
            logger.catching( ex );
            return false;
        }
    }

    /**
     * Returns the Hoster that matches the given URL or {@code null}, if none matches.
     * 
     * @param url
     *            the URL to check the Hoster against
     * @return the Hoster that has the given URL; when none is found {@code null}
     */
    public static Hoster tryGetHostByURL( final URL url ) {
        final Pattern www = Pattern.compile( "www\\..+" );
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
     * Returns the Hoster that matches the given URL. Throws an exception, if none matches.
     * 
     * @param url
     *            the URL to check the Hoster against
     * @return the Hoster that has the given URL; throws an exception when none is found
     * @throws NoHosterFoundException
     *             if no Hoster matches the given URL
     */
    public static Hoster getHostByURL( final URL url ) throws NoHosterFoundException {
        Hoster hoster = tryGetHostByURL( url );
        if ( hoster == null ) {
            throw new NoHosterFoundException( "Hoster for " + url + " cannot be found" );
        }
        return hoster;
    }

    /**
     * Returns an {@linkplain ImmutableSet} containing all registered {@link Hoster} in an unspecified order.
     * 
     * @return the registered Hoster
     * @see #sortedValues()
     * @see #sortedValues(Comparator)
     */
    public static Set<Hoster> values() {
        return ImmutableSet.copyOf( hosters );
    }

    /**
     * Returns a {@link SortedSet} containing all registered {@link Hoster}. The {@link Comparator} used for sorting is
     * {@link Hoster#NAME_COMPARATOR}
     * 
     * @return the registered Hoster
     * @see #values()
     * @see #sortedValues(Comparator)
     */
    public static SortedSet<Hoster> sortedValues() {
        return sortedValues( Hoster.NAME_COMPARATOR );
    }

    /**
     * Returns a {@link SortedSet} containing all registered {@link Hoster}. The given {@link Comparator} is used for sorting.
     * 
     * @param comparator
     *            the comparator to sort with
     * @return the registered Hoster
     * @see #values()
     * @see #sortedValues()
     */
    public static SortedSet<Hoster> sortedValues( final Comparator<Hoster> comparator ) {
        return ImmutableSortedSet.copyOf( comparator, hosters );
    }

    private Hosters() {
        // avoid instantiation
    }
}
