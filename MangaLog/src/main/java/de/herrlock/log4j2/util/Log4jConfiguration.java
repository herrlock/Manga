package de.herrlock.log4j2.util;

import java.util.List;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AppenderRef;

import de.herrlock.log4j2.filter.LevelFilter;

public final class Log4jConfiguration {
    private static final Logger logger = LogManager.getLogger();

    public static void changeLevelFilterLevel( final String optionValue ) {
        Level level = Level.toLevel( optionValue, Level.INFO );
        changeLevelFilterLevel( level );
    }

    public static void changeLevelFilterLevel( final Level level ) {
        LoggerContext context = ( LoggerContext ) LogManager.getContext( false );
        List<AppenderRef> appenderRefs = context.getConfiguration().getRootLogger().getAppenderRefs();
        for ( AppenderRef appenderRef : appenderRefs ) {
            if ( "ConsoleLogger".equals( appenderRef.getRef() ) ) {
                Filter rootFilter = appenderRef.getFilter();
                if ( rootFilter instanceof LevelFilter ) {
                    LevelFilter levelFilter = ( LevelFilter ) rootFilter;
                    levelFilter.setLevel( level );
                    context.updateLoggers();
                    logger.debug( "set log-level to {}", level );
                } else {
                    logger.debug( "cannot find LevelFilter" );
                }
            } else {
                logger.debug( "Not ConsoleLogger: {}", appenderRef );
            }
        }
    }

    private Log4jConfiguration() {
        // prevent instantiation
    }
}
