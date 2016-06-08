package de.herrlock.log4j2.filter;

import java.text.MessageFormat;
import java.util.Objects;

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
 * A simple Log4j2-Filter that filters with an additional log-level. Taken and modified for personal requirements from
 * https://logging.apache.org/log4j/2.0/manual/extending.html#Filters
 */
@Plugin( name = "LevelFilter", category = "Core", elementType = "filter", printObject = true )
public final class LevelFilter extends AbstractFilter {
    private Level filterLevel;

    private LevelFilter( final Level level, final Result onMatch, final Result onMismatch ) {
        super( onMatch, onMismatch );
        this.filterLevel = Objects.requireNonNull( level );
    }

    /**
     * Simply calls {@link #filter(Level)} with the passed {@link Level}.
     */
    @Override
    public Result filter( final Logger logger, final Level level, final Marker marker, final String msg,
        final Object... params ) {
        return filter( level );
    }

    /**
     * Simply calls {@link #filter(Level)} with the passed {@link Level}.
     */
    @Override
    public Result filter( final Logger logger, final Level level, final Marker marker, final Object msg, final Throwable t ) {
        return filter( level );
    }

    /**
     * Simply calls {@link #filter(Level)} with the passed {@link Level}.
     */
    @Override
    public Result filter( final Logger logger, final Level level, final Marker marker, final Message msg, final Throwable t ) {
        return filter( level );
    }

    /**
     * Simply calls {@link #filter(Level)} with the level from the passed {@link LogEvent}.
     */
    @Override
    public Result filter( final LogEvent event ) {
        return filter( event.getLevel() );
    }

    /**
     * Compares the given Level with the level currently set in this filter. If the given level is more specific than this level
     * {@code onMatch} is returned. Otherwise {@code onMismatch} is returned.
     * 
     * @param level
     *            The level to filter on.
     * @return The Result of filtering.
     */
    public Result filter( final Level level ) {
        return level.isMoreSpecificThan( this.filterLevel ) ? this.onMatch : this.onMismatch;
    }

    /**
     * Setter for the {@link Level} of this filter.
     * 
     * @param level
     *            the new {@linkplain Level}
     */
    public void setLevel( final Level level ) {
        this.filterLevel = level;
    }

    @Override
    public String toString() {
        return MessageFormat.format( "LevelFilter: {0}", this.filterLevel );
    }

    /**
     * Create a ThresholdFilter.
     * 
     * @param level
     *            The log Level to filter with.
     * @param onMatch
     *            The action to return on a match.
     * @param onMismatch
     *            The action to return on a mismatch.
     * @return The created ThresholdFilter.
     */
    @PluginFactory
    public static LevelFilter createFilter( @PluginAttribute( value = "level", defaultString = "ERROR" ) final Level level,
        @PluginAttribute( value = "onMatch", defaultString = "NEUTRAL" ) final Result onMatch,
        @PluginAttribute( value = "onMismatch", defaultString = "DENY" ) final Result onMismatch ) {
        return new LevelFilter( level, onMatch, onMismatch );
    }
}
