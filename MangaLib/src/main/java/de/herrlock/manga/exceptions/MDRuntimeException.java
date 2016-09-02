package de.herrlock.manga.exceptions;

/**
 * A general custom RuntimeException
 * 
 * @author HerrLock
 */
public class MDRuntimeException extends RuntimeException {

    /**
     * A new MDRuntimeException without detailed message or cause
     * 
     * @deprecated should not be used since there will be no detail-message
     */
    @Deprecated
    public MDRuntimeException() {
        super();
    }

    /**
     * A new MDRuntimeException with a detailed message
     * 
     * @param message
     *            a detailmessage why this exception occurred
     */
    public MDRuntimeException( final String message ) {
        super( message );
    }

    /**
     * A new MDRuntimeException with a cause
     * 
     * @param cause
     *            the cause of this exception
     */
    public MDRuntimeException( final Throwable cause ) {
        super( cause );
    }

    /**
     * A new MDRuntimeException with a detailed message and cause
     * 
     * @param message
     *            a detailmessage why this exception occurred
     * @param cause
     *            the cause of this exception
     */
    public MDRuntimeException( final String message, final Throwable cause ) {
        super( message, cause );
    }

}
