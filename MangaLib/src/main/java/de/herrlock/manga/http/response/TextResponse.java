package de.herrlock.manga.http.response;

import java.nio.charset.StandardCharsets;

/**
 * @author HerrLock
 */
public class TextResponse extends Response {

    private String text;

    public TextResponse() {
        this( "" );
    }

    public TextResponse( String text ) {
        this( -1, text );
    }

    public TextResponse( int code ) {
        this( code, "" );
    }

    public TextResponse( int code, String text ) {
        super( code );
        setText( text );
    }

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
