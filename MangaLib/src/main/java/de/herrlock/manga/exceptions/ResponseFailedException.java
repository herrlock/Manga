package de.herrlock.manga.exceptions;

/**
 * @author Jan Rau
 *
 */
public class ResponseFailedException extends ResponseHandlerException {

    private static final long serialVersionUID = 1L;

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
