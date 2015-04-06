package de.herrlock.exceptions;

/**
 * an exception that indicates that sth. happened while initializing the download, so the application can stay opened.
 * 
 * @author HerrLock
 */
public class InitializeException extends RuntimeException {

    public InitializeException() {
        super();
    }
    public InitializeException( String message ) {
        super( message );
    }
    public InitializeException( Exception cause ) {
        super( cause );
    }
    public InitializeException( String message, Exception cause ) {
        super( message, cause );
    }

}
