package de.herrlock.manga.http.response;

import java.nio.charset.StandardCharsets;

import org.apache.http.HttpStatus;
import org.apache.http.entity.StringEntity;

/**
 * A {@link Response} that results in plain text
 * 
 * @author HerrLock
 */
public class TextResponse extends Response {

    /**
     * Creates a new TextResponse with the given status and text
     * 
     * @param httpStatus
     *            the http-response-code (see {@linkplain HttpStatus} for constants)
     * @param text
     *            the response-text
     */
    public TextResponse( final int httpStatus, final String text ) {
        super( httpStatus, new StringEntity( text, StandardCharsets.UTF_8 ) );
    }

}
