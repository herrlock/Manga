package de.herrlock.manga.http;

import java.util.Date;

/**
 * @author HerrLock
 */
public abstract class Response {
    private int code;

    public Response() {
        this( -1 );
    }

    public Response( int code ) {
        this.code = code;
    }

    public Response setCode( int code ) {
        this.code = code;
        return this;
    }

    protected abstract Object getData();
    protected abstract String getContentType();

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append( "HTTP/1.1 " );
        sb.append( this.code );
        sb.append( "\nContent-Type: " );
        sb.append( getContentType() );
        sb.append( "\nDate: " );
        sb.append( new Date().toString() );
        sb.append( "\n\n" );
        sb.append( getData() );
        return sb.toString();
    }
}
