package de.herrlock.manga.http.response;

import java.nio.charset.StandardCharsets;

import org.jsoup.nodes.Document;

/**
 * A {@link Response} that results in a Jsoup-{@link Document}
 * 
 * @author HerrLock
 */
public class DocumentResponse extends Response {
    private Document document;

    /**
     * Creates an empty DocumentResponse
     */
    public DocumentResponse() {
        this( null );
    }

    /**
     * Creates a TextResponse with dummy-code {@code -1} and the given Document
     * 
     * @param document
     *            the response-document
     */
    public DocumentResponse( Document document ) {
        this( -1, document );
    }

    /**
     * Creates a TextResponse with the given response-core and no document
     * 
     * @param code
     *            the response-code
     */
    public DocumentResponse( int code ) {
        this( code, null );
    }

    /**
     * Creates a TextResponse with the given response-core and the given document
     * 
     * @param code
     *            the response-code
     * @param document
     *            the response-document
     */
    public DocumentResponse( int code, Document document ) {
        super( code );
        setDocument( document );
    }

    /**
     * replaces the current response-document with a new one
     * 
     * @param doc
     *            the new doc
     * @return {@code this} to enable method-chaining
     */
    public DocumentResponse setDocument( Document doc ) {
        this.document = doc;
        return this;
    }

    @Override
    public byte[] getData() {
        return this.document.toString().getBytes( StandardCharsets.UTF_8 );
    }

    @Override
    protected String getContentType() {
        return "text/html";
    }

}
