package de.herrlock.manga.exceptions;

/**
 * an exception
 * 
 * @author HerrLock
 */
public final class MyException extends RuntimeException {
    // TODO: rename this Exception

    /**
     * A new MyException without detailed message or cause
     * 
     * @deprecated should not be used since there will be no detail-message
     */
    @Deprecated
    public MyException() {
        super();
    }

    /**
     * A new MyException with a detailed message
     * 
     * @param message
     *            a detailmessage why this exception occurred
     */
    public MyException( final String message ) {
        super( message );
    }

    /**
     * A new MyException with a cause
     * 
     * @param cause
     *            the cause of this exception
     */
    public MyException( final Throwable cause ) {
        super( cause );
    }

    /**
     * A new MyException with a detailed message and cause
     * 
     * @param message
     *            a detailmessage why this exception occurred
     * @param cause
     *            the cause of this exception
     */
    public MyException( final String message, final Throwable cause ) {
        super( message, cause );
    }

}
