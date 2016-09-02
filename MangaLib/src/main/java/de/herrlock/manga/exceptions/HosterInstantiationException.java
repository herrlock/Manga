package de.herrlock.manga.exceptions;

/**
 * An Exception indicating, that a Hoster could not be created.
 * 
 * @author HerrLock
 */
public final class HosterInstantiationException extends MDException {

    @Deprecated
    public HosterInstantiationException() {
        super();
    }

    public HosterInstantiationException( final String message ) {
        super( message );
    }

    public HosterInstantiationException( final Throwable cause ) {
        super( cause );
    }

    public HosterInstantiationException( final String message, final Throwable cause ) {
        super( message, cause );
    }

}
