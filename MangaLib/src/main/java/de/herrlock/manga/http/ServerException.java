package de.herrlock.manga.http;

/**
 * @author HerrLock
 */
public class ServerException extends RuntimeException {
    public ServerException() {
        super();
    }

    public ServerException( String message ) {
        super( message );
    }

    public ServerException( Exception cause ) {
        super( cause );
    }

    public ServerException( String message, Exception cause ) {
        super( message, cause );
    }

}
