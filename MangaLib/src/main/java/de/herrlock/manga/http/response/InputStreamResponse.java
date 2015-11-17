package de.herrlock.manga.http.response;

import java.io.InputStream;

import org.apache.http.HttpStatus;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.InputStreamEntity;

/**
 * A {@link Response} that returns an image.
 * 
 * @author HerrLock
 */
public class InputStreamResponse extends Response {

    /**
     * Creates a new InputStreamResponse with the content to be loaded from the given InputStream
     * 
     * @param inputStream
     *            the {@link InputStream} to load the content from
     * @param contentType
     *            the content-type of the content
     */
    public InputStreamResponse( final InputStream inputStream, final String contentType ) {
        super( HttpStatus.SC_OK, new InputStreamEntity( inputStream, ContentType.create( contentType ) ) );
    }

    /**
     * Creates a new InputStreamResponse with the content to be loaded from the given InputStream
     * 
     * @param inputStream
     *            the {@link InputStream} to load the content from
     * @param contentType
     *            the content-type of the content
     */
    public InputStreamResponse( final InputStream inputStream, final ContentType contentType ) {
        super( HttpStatus.SC_OK, new InputStreamEntity( inputStream, contentType ) );
    }

}
