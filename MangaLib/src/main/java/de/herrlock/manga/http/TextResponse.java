package de.herrlock.manga.http;

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
    protected Object getData() {
        return this.text;
    }

    @Override
    protected String getCotentType() {
        return "text";
    }

}
