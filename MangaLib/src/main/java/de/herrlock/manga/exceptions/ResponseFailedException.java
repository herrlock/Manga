package de.herrlock.manga.exceptions;

/**
 * @author Jan Rau
 *
 */
public class ResponseFailedException extends ResponseHandlerException {

    public ResponseFailedException( final String message ) {
        super( message );
    }

    public ResponseFailedException( final Throwable cause ) {
        super( cause );
    }

    public ResponseFailedException( final String message, final Throwable cause ) {
        super( message, cause );
    }

}
