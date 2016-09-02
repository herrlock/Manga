package de.herrlock.manga.exceptions;

/**
 * An EXception indicating, that a Hoster could not be created.
 * 
 * @author HerrLock
 */
public final class HosterInstantiationException extends MDException {

    public HosterInstantiationException( final String message ) {
        super( message );
    }

}
