package de.herrlock.manga.http.response;

import java.nio.charset.StandardCharsets;

/**
 * A {@link Response} that results in plain text
 * 
 * @author HerrLock
 */
public class TextResponse extends Response {

    private String text;

    /**
     * Creates an empty TextResponse
     */
    public TextResponse() {
        this( "" );
    }

    /**
     * Creates a TextResponse with dummy-code {@code -1} and the given text
     * 
     * @param text
     *            the response-text
     */
    public TextResponse( String text ) {
        this( -1, text );
    }

    /**
     * Creates a TextResponse with the given response-core and no text
     * 
     * @param code
     *            the response-code
     */
    public TextResponse( int code ) {
        this( code, "" );
    }

    /**
     * Creates a TextResponse with the given response-core and the given text
     * 
     * @param code
     *            the response-code
     * @param text
     *            the response-text
     */
    public TextResponse( int code, String text ) {
        super( code );
        setText( text );
    }

    /**
     * replaces the current response-text with a new one
     * 
     * @param text
     *            the new text
     */
    public void setText( String text ) {
        this.text = text;
    }

    @Override
    public byte[] getData() {
        return this.text.getBytes( StandardCharsets.UTF_8 );
    }

    @Override
    protected String getContentType() {
        return "text";
    }

}
