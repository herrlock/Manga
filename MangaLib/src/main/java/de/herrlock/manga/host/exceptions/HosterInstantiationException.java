package de.herrlock.manga.host.exceptions;

import de.herrlock.manga.exceptions.MDException;

/**
 * An Exception indicating, that a Hoster could not be created.
 * 
 * @author HerrLock
 */
public final class HosterInstantiationException extends MDException {

    private static final long serialVersionUID = 1L;

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
