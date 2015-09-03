package de.herrlock.manga.http;

/**
 * An Exception to indicate something went wrong in the {@link Server}
 * 
 * @author HerrLock
 */
public class ServerException extends RuntimeException {
    /**
     * A default ServerException without message or cause
     */
    public ServerException() {
        super();
    }

    /**
     * A default ServerException with a detail-message
     * 
     * @param message
     *            a detail-message to provide further information
     */
    public ServerException( String message ) {
        super( message );
    }

    /**
     * A default ServerException with a cause
     * 
     * @param cause
     *            an Exception that caused this Exception
     */
    public ServerException( Exception cause ) {
        super( cause );
    }

    /**
     * A default ServerException with a detail-message and a cause
     * 
     * @param message
     *            a detail-message to provide further information
     * @param cause
     *            an Exception that caused this Exception
     */
    public ServerException( String message, Exception cause ) {
        super( message, cause );
    }

}
