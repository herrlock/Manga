package de.herrlock.manga.config;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author HerrLock
 */
public final class ConfigUtils {

    private static final Logger logger = LogManager.getLogger();

    public static InputStream getStreamFor( final String path ) throws IOException {
        logger.entry( path );
        if ( path == null || "".equals( path ) || "-".equals( path ) ) {
            logger.debug( "Returning System.in" );
            return System.in;
        }
        logger.debug( "Returning FileInputStream" );
        return Files.newInputStream( Paths.get( path ) );
    }

    private ConfigUtils() {
        // suppress instantiation
    }

}
