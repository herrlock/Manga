package de.herrlock.manga.exceptions;

/**
 * A general custom checked Exception
 * 
 * @author HerrLock
 */
public class MDException extends Exception {

    /**
     * A new MDException without detailed message or cause
     * 
     * @deprecated should not be used since there will be no detail-message
     */
    @Deprecated
    public MDException() {
        super();
    }

    /**
     * A new MDException with a detailed message
     * 
     * @param message
     *            a detailmessage why this exception occurred
     */
    public MDException( final String message ) {
        super( message );
    }

    /**
     * A new MDException with a cause
     * 
     * @param cause
     *            the cause of this exception
     */
    public MDException( final Throwable cause ) {
        super( cause );
    }

    /**
     * A new MDException with a detailed message and cause
     * 
     * @param message
     *            a detailmessage why this exception occurred
     * @param cause
     *            the cause of this exception
     */
    public MDException( final String message, final Throwable cause ) {
        super( message, cause );
    }

}
