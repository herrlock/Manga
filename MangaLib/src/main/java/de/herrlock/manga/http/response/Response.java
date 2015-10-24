package de.herrlock.manga.http.response;

import java.text.MessageFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

/**
 * A Http-Response. Can be subclassed to handle special requirements. The response is always returned as {@code byte[]} in the
 * method {@link #getData()}
 * 
 * @author HerrLock
 */
public abstract class Response {
    private int code;
    private final Map<String, String> header = new HashMap<>();

    /**
     * a Response with the dummy-responsecode {@code -1}
     */
    public Response() {
        this( -1 );
    }

    /**
     * a Response with given responsecode
     * 
     * @param code
     *            the http-responsecode to set
     */
    public Response( final int code ) {
        this.code = code;
    }

    /**
     * replaces the current http-statuscode with a new one
     * 
     * @param code
     *            the new code to set
     * @return {@code this} to enable method-chaining
     */
    public Response setCode( final int code ) {
        this.code = code;
        return this;
    }

    /**
     * sets a http-header key-value-pair
     * 
     * @param key
     *            the pair's key
     * @param value
     *            the pair's value
     * @return {@code this} to enable method-chaining
     */
    public Response setHeader( final String key, final String value ) {
        this.header.put( key, value );
        return this;
    }

    /**
     * @return the response-body as bytes
     */
    public abstract byte[] getData();

    /**
     * @return the response's content-type
     */
    protected abstract String getContentType();

    /**
     * creates a HTTP/1.1-representation of the current Response-headerlines
     * 
     * @return a string-representation of the current Response-headers
     */
    public String createHTTPHeader() {
        StringBuilder sb = new StringBuilder()//
            .append( "HTTP/1.1 " )//
            .append( this.code )//
            .append( "\nContent-Type: " )//
            .append( this.header.containsKey( "Content-Type" ) ? this.header.get( "Content-Type" ) : getContentType() )//
            .append( "\nDate: " )//
            .append( this.header.containsKey( "Date" ) ? this.header.get( "Date" ) : new Date().toString() );
        for ( Entry<String, String> entry : this.header.entrySet() ) {
            sb.append( MessageFormat.format( "\n{0}: {1}", entry.getKey(), entry.getValue() ) );
        }

        sb.append( "\n\n" );
        return sb.toString();
    }
}
