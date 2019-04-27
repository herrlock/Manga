package de.herrlock.manga.host.exceptions;

import de.herrlock.manga.exceptions.MDException;

/**
 * @author HerrLock
 */
public class NoHosterFoundException extends MDException {

    private static final long serialVersionUID = 1L;

    @Deprecated
    public NoHosterFoundException() {
        super();
    }

    public NoHosterFoundException( final String message ) {
        super( message );
    }

    public NoHosterFoundException( final Throwable cause ) {
        super( cause );
    }

    public NoHosterFoundException( final String message, final Throwable cause ) {
        super( message, cause );
    }

}
