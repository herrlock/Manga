package de.herrlock.manga.exceptions;

/**
 * @author Jan Rau
 *
 */
public class ResponseHandlerException extends RuntimeException {

    public ResponseHandlerException( final String message ) {
        super( message );
    }

    public ResponseHandlerException( final Throwable cause ) {
        super( cause );
    }

    public ResponseHandlerException( final String message, final Throwable cause ) {
        super( message, cause );
    }

}
