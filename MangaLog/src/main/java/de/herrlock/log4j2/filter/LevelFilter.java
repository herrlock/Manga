package de.herrlock.log4j2.filter;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.Marker;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.Logger;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.AbstractFilter;
import org.apache.logging.log4j.message.Message;

/**
 * A simple Log4j2-Filter that filters with an additional log-level. Taken (and slightly modified) from
 * https://logging.apache.org/log4j/2.0/manual/extending.html#Filters
 */
@Plugin( name = "LevelFilter", category = "Core", elementType = "filter", printObject = true )
public final class LevelFilter extends AbstractFilter {
    private static final long serialVersionUID = 1L;

    private final Level level;

    private LevelFilter( final Level level, final Result onMatch, final Result onMismatch ) {
        super( onMatch, onMismatch );
        this.level = level;
    }

    @Override
    public Result filter( final Logger logger, final Level level, final Marker marker, final String msg,
        final Object... params ) {
        return filter( level );
    }

    @Override
    public Result filter( final Logger logger, final Level level, final Marker marker, final Object msg, final Throwable t ) {
        return filter( level );
    }

    @Override
    public Result filter( final Logger logger, final Level level, final Marker marker, final Message msg, final Throwable t ) {
        return filter( level );
    }

    @Override
    public Result filter( final LogEvent event ) {
        return filter( event.getLevel() );
    }

    private Result filter( final Level level ) {
        return level.compareTo( this.level ) <= 0 ? this.onMatch : this.onMismatch;
    }

    @Override
    public String toString() {
        return java.text.MessageFormat.format( "LevelFilter: {0}", this.level );
    }

    /**
     * Create a ThresholdFilter.
     * 
     * @param level
     *            The log Level.
     * @param onMatch
     *            The action to take on a match.
     * @param onMismatch
     *            The action to take on a mismatch.
     * @return The created ThresholdFilter.
     */
    @PluginFactory
    public static LevelFilter createFilter( @PluginAttribute( value = "level", defaultString = "ERROR" ) final Level level,
        @PluginAttribute( value = "onMatch", defaultString = "NEUTRAL" ) final Result onMatch,
        @PluginAttribute( value = "onMismatch", defaultString = "DENY" ) final Result onMismatch ) {
        return new LevelFilter( level, onMatch, onMismatch );
    }
}
