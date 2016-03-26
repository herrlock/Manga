package de.herrlock.manga.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public final class Lookup<T> {
    private static final Logger logger = LogManager.getLogger();

    private final Class<T> clazz;
    private final Set<Class<T>> allClasses;

    public static <T> Collection<? extends Class<T>> lookupAll( final Class<T> clazz ) {
        Lookup<T> lookup = new Lookup<>( clazz );
        return lookup.getAllClasses();
    }

    private Lookup( final Class<T> clazz ) {
        this.clazz = clazz;
        List<URL> lookupFiles = lookupFiles();
        Set<String> readFiles = readFiles( lookupFiles );
        this.allClasses = getClasses( readFiles );
    }

    public Set<Class<T>> getAllClasses() {
        return this.allClasses;
    }

    private List<URL> lookupFiles() {
        final List<URL> urls = new ArrayList<>();
        try {
            Enumeration<URL> systemResources = ClassLoader.getSystemResources( "META-INF/services/" + this.clazz.getName() );
            while ( systemResources.hasMoreElements() ) {
                urls.add( systemResources.nextElement() );
            }
        } catch ( IOException ex ) {
            logger.error( ex );
        }
        return urls;
    }

    private Set<String> readFiles( final List<URL> lookupFiles ) {
        final Set<String> result = new HashSet<>();
        for ( URL url : lookupFiles ) {
            result.addAll( lines( url ) );
        }
        return result;
    }

    private Set<String> lines( final URL url ) {
        final Set<String> result = new HashSet<>();
        try ( BufferedReader reader = new BufferedReader( new InputStreamReader( url.openStream(), StandardCharsets.UTF_8 ) ) ) {
            String nextline;
            while ( ( nextline = reader.readLine() ) != null ) {
                int commentStart = nextline.indexOf( '#' );
                String checkedLine = nextline;
                if ( commentStart >= 0 ) {
                    checkedLine = nextline.substring( 0, commentStart );
                }
                checkedLine = checkedLine.trim();
                result.add( checkedLine );
            }
        } catch ( IOException ex ) {
            logger.error( ex );
        }
        return result;
    }

    @SuppressWarnings( "unchecked" )
    private Set<Class<T>> getClasses( final Collection<String> names ) {
        final Set<Class<T>> result = new HashSet<>();
        for ( String className : names ) {
            try {
                Class<?> c = Class.forName( className );
                if ( this.clazz.isAssignableFrom( c ) ) {
                    result.add( ( Class<T> ) c );
                }
            } catch ( ClassNotFoundException ex ) {
                logger.warn( "Could not find class {}", className );
                logger.error( ex );
            }
        }
        return result;
    }
}
