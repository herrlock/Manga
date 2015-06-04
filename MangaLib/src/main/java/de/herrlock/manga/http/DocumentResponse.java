package de.herrlock.manga.http;

import org.jsoup.nodes.Document;

/**
 * @author HerrLock
 */
public class DocumentResponse extends Response {
    private Document document;

    public DocumentResponse() {
        this( null );
    }

    public DocumentResponse( Document document ) {
        this( -1, document );
    }

    public DocumentResponse( int code ) {
        this( code, null );
    }

    public DocumentResponse( int code, Document document ) {
        super( code );
        setDocument( document );
    }

    public DocumentResponse setDocument( Document doc ) {
        this.document = doc;
        return this;
    }

    @Override
    protected Object getData() {
        return this.document;
    }

    @Override
    protected String getContentType() {
        return "text/html";
    }

}
