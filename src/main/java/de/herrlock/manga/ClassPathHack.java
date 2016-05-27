package de.herrlock.manga;

import java.io.File;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.file.Paths;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.Iterator;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import com.google.common.collect.Iterators;

import de.herrlock.manga.exceptions.MDRuntimeException;
import javafx.application.Application;

/**
 * A class that searches for the jfxrt-jar and adds it to the System-Classloder's resources. <br>
 * Taken (and customied) from <a href="http://stackoverflow.com/a/60766/3680684">Stackoverflow</a>
 */
public final class ClassPathHack {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Tries to load the class {@linkplain Application} twice. When the first try succeeds everything is perfect. When the first
     * try fails (ClassNotFoundException) the jfxrt-jar is searched and (probably) added to the classpath. Then the class is
     * loaded a second time. A resulting ClassNotFoundException is thrown so the caller can see what happened.
     * 
     * @throws ClassNotFoundException
     *             When the class {@linkplain Application} cannot be found after the jfxrt-jar has been tried to add to the
     *             classpath.
     */
    public static void makeSureJfxrtLoaded() throws ClassNotFoundException {
        try {
            tryToLoadJavafxApplication();
            logger.debug( "Loaded {} at first try", Application.class );
        } catch ( ClassNotFoundException ex ) {
            logger.debug( "Could not find class, trying to add the jar" );
            addJarToSystemClassloader();
            tryToLoadJavafxApplication();
            logger.debug( "Loaded {} after adding the jar", Application.class );
        }
    }

    private static void tryToLoadJavafxApplication() throws ClassNotFoundException {
        Class.forName( "javafx.application.Application" );
    }

    private static void addJarToSystemClassloader() {
        final File javaHomeFolder = Paths.get( System.getProperty( "java.home" ) ).toFile();
        Iterator<File> iterator = FileUtils.iterateFiles( javaHomeFolder, new String[] {
            "jar"
        }, true );
        Optional<File> jfxrtOptional = Iterators.tryFind( iterator, new FilenamePredicate( "jfxrt.jar" ) );

        if ( jfxrtOptional.isPresent() ) {
            File jfxrt = jfxrtOptional.get();
            logger.debug( "jfxrt.jar: {}", jfxrt );
            try {
                addURI( jfxrt.toURI() );
            } catch ( MalformedURLException malformedUrlException ) {
                logger.error( malformedUrlException );
            }
        } else {
            logger.warn( "Cannot add jfxrt.jar, no jar found" );
        }
    }

    private static void addURI( final URI uri ) throws MalformedURLException {
        addURL( uri.toURL() );
    }

    private static void addURL( final URL url ) {
        logger.trace( url );
        final URLClassLoader sysloader = ( URLClassLoader ) ClassLoader.getSystemClassLoader();
        final Class<? extends ClassLoader> sysclass = URLClassLoader.class;

        AccessController.doPrivileged( new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    Method method = sysclass.getDeclaredMethod( "addURL", URL.class );
                    method.setAccessible( true );
                    method.invoke( sysloader, new Object[] {
                        url
                    } );
                } catch ( IllegalArgumentException | ReflectiveOperationException | SecurityException e ) {
                    throw new MDRuntimeException( e );
                }
                return null;
            }
        } );
    }

    private ClassPathHack() {
        // not used
    }

    public static class FilenamePredicate implements Predicate<File> {
        private final String filename;
        private final boolean ignoreCase;

        public FilenamePredicate( final String filename ) {
            this( filename, false );
        }

        public FilenamePredicate( final String filename, final boolean ignoreCase ) {
            this.filename = filename;
            this.ignoreCase = ignoreCase;
        }

        @Override
        public boolean apply( final File input ) {
            boolean isNull = input == null;
            if ( isNull ) {
                return false;
            }
            String name = input.getName();
            return this.ignoreCase ? name.equalsIgnoreCase( this.filename ) : name.equals( this.filename );
        }
    }
}
