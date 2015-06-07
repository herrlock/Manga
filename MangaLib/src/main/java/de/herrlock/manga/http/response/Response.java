package de.herrlock.manga.http.response;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * @author HerrLock
 */
public abstract class Response {
    private int code;
    private final Map<String, String> header = new HashMap<>();

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

    public Response setHeader( String key, String value ) {
        this.header.put( key, value );
        return this;
    }

    public abstract byte[] getData();
    protected abstract String getContentType();

    public String createHTTPHeader() {
        StringBuilder sb = new StringBuilder();
        sb.append( "HTTP/1.1 " );
        sb.append( this.code );
        sb.append( "\nContent-Type: " );
        sb.append( this.header.containsKey( "Content-Type" ) ? this.header.get( "Content-Type" ) : getContentType() );
        sb.append( "\nDate: " );
        sb.append( this.header.containsKey( "Date" ) ? this.header.get( "Date" ) : new Date().toString() );
        for ( Entry<String, String> entry : this.header.entrySet() ) {
            sb.append( "\n" );
            sb.append( entry.getKey() );
            sb.append( ": " );
            sb.append( entry.getValue() );
        }

        sb.append( "\n\n" );
        return sb.toString();
    }
}
