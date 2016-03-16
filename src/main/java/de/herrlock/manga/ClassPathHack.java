package de.herrlock.manga;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
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
 * <a href="http://stackoverflow.com/a/60766/3680684">Source (Stackoverflow)</a>
 */
public final class ClassPathHack {
    private static final Logger logger = LogManager.getLogger();

    public static void validateJfxrtLoaded() throws IOException {
        try {
            Class.forName( "javafx.application.Application" );
            logger.info( "Loaded {} at first try", Application.class );
        } catch ( ClassNotFoundException ex ) {
            logger.info( "Could not find class, trying to add the jar" );

            final File javaHomeFolder = Paths.get( System.getProperty( "java.home" ) ).toFile();
            Iterator<File> iterator = FileUtils.iterateFiles( javaHomeFolder, new String[] {
                "jar"
            }, true );
            Optional<File> jfxrtOptional = Iterators.tryFind( iterator, new Predicate<File>() {
                @Override
                public boolean apply( final File input ) {
                    return input != null && input.getName().equals( "jfxrt.jar" );
                }
            } );

            if ( jfxrtOptional.isPresent() ) {
                File jfxrt = jfxrtOptional.get();
                logger.debug( "jfxrt.jar: {}", jfxrt );
                addURL( jfxrt.toURI().toURL() );
            } else {
                logger.warn( "Cannot add jfxrt.jar, no jar found" );
            }
            try {
                Class.forName( "javafx.application.Application" );
                logger.info( "Loaded {} after adding the jar", Application.class );
            } catch ( ClassNotFoundException ex2 ) {
                logger.error( "Failed to load class (2) => failing" );
            }
        }
    }

    private static void addURL( final URL u ) {
        logger.trace( u );
        final URLClassLoader sysloader = ( URLClassLoader ) ClassLoader.getSystemClassLoader();
        final Class<? extends ClassLoader> sysclass = URLClassLoader.class;

        AccessController.doPrivileged( new PrivilegedAction<Object>() {
            @Override
            public Object run() {
                try {
                    Method method = sysclass.getDeclaredMethod( "addURL", URL.class );
                    method.setAccessible( true );
                    method.invoke( sysloader, new Object[] {
                        u
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
}
