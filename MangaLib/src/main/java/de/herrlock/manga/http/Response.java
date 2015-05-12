package de.herrlock.manga.http;

import java.util.Date;

import org.jsoup.nodes.Document;

/**
 * @author HerrLock
 */
public class Response {
    private Document document;
    private int code;

    public Response() {
        this( Document.createShell( "localhost" ) );
    }

    public Response( Document doc ) {
        this( -1, doc );
    }

    public Response( int code, Document doc ) {
        setDocument( doc );
        setCode( code );
    }

    public Response setCode( int code ) {
        this.code = code;
        return this;
    }

    public Response setDocument( Document doc ) {
        this.document = doc;
        return this;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "HTTP/1.1 " );
        sb.append( this.code );
        sb.append( "\n" );
        sb.append( "Content-Type: text/html" );
        sb.append( "\n" );
        sb.append( "Date: " );
        sb.append( new Date().toString() );
        sb.append( "\n\n" );
        sb.append( this.document );
        return sb.toString();
    }
}
