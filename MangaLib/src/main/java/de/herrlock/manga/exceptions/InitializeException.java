package de.herrlock.manga.exceptions;

/**
 * an exception that indicates that sth. happened while initializing the download, so the application can stay opened.
 * 
 * @author HerrLock
 */
public final class InitializeException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    /**
     * A new InitializeException without detailed message or cause
     * 
     * @deprecated should not be used since there will be no detail-message
     */
    @Deprecated
    public InitializeException() {
        super();
    }

    /**
     * A new InitializeException with a detailed message
     * 
     * @param message
     *            a detailmessage why this exception occurred
     */
    public InitializeException( final String message ) {
        super( message );
    }

    /**
     * A new InitializeException with a cause
     * 
     * @param cause
     *            the cause of this exception
     */
    public InitializeException( final Throwable cause ) {
        super( cause );
    }

    /**
     * A new InitializeException with a detailed message and cause
     * 
     * @param message
     *            a detailmessage why this exception occurred
     * @param cause
     *            the cause of this exception
     */
    public InitializeException( final String message, final Throwable cause ) {
        super( message, cause );
    }

}
