package de.herrlock.manga.http.server.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.eclipse.jetty.util.log.AbstractLogger;
import org.eclipse.jetty.util.log.Logger;

/**
 * @author Herrlock
 */
public class Log4j2Bridge extends AbstractLogger {

    private final org.apache.logging.log4j.Logger logger;
    private final String name;

    public Log4j2Bridge( final Class<?> clazz ) {
        this( clazz.getName() );
    }

    public Log4j2Bridge( final String name ) {
        this.logger = LogManager.getLogger( name );
        this.name = name;
    }

    @Override
    protected Logger newLogger( final String fullname ) {
        return new Log4j2Bridge( fullname );
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public void warn( final String msg, final Object... args ) {
        this.logger.warn( msg, args );
    }

    @Override
    public void warn( final Throwable thrown ) {
        this.logger.catching( Level.WARN, thrown );
    }

    @Override
    public void warn( final String msg, final Throwable thrown ) {
        this.logger.warn( msg, thrown );
    }

    @Override
    public void info( final String msg, final Object... args ) {
        this.logger.info( msg, args );
    }

    @Override
    public void info( final Throwable thrown ) {
        this.logger.catching( Level.INFO, thrown );
    }

    @Override
    public void info( final String msg, final Throwable thrown ) {
        this.logger.info( msg, thrown );
    }

    @Override
    public boolean isDebugEnabled() {
        return false;
        // return this.logger.isDebugEnabled();
    }

    @Override
    public void setDebugEnabled( final boolean enabled ) {
        // TODO
    }

    @Override
    public void debug( final String msg, final Object... args ) {
        this.logger.debug( msg, args );
    }

    @Override
    public void debug( final Throwable thrown ) {
        this.logger.catching( Level.DEBUG, thrown );
    }

    @Override
    public void debug( final String msg, final Throwable thrown ) {
        this.logger.debug( msg, thrown );
    }

    @Override
    public void ignore( final Throwable ignored ) {
        this.logger.catching( Level.TRACE, ignored );
    }

}
