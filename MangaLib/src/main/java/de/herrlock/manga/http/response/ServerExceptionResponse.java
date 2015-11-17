package de.herrlock.manga.http.response;

import org.apache.http.HttpStatus;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * A {@link Response} that indiates an Exception on the {@link de.herrlock.manga.http.Server Server}
 * 
 * @author HerrLock
 */
public class ServerExceptionResponse extends DocumentResponse {

    /**
     * Creates a {@link ServerExceptionResponse} with the given cause
     * 
     * @param ex
     *            the cause of this Response
     */
    public ServerExceptionResponse( final Exception ex ) {
        super( HttpStatus.SC_INTERNAL_SERVER_ERROR, createDocument( ex ) );
    }

    private static Document createDocument( final Exception ex ) {
        Document doc = Document.createShell( "http://localhost" );
        doc.appendElement( "head" ).appendElement( "title" ).text( "Internal Error" );
        Element div = doc.appendElement( "body" ).appendElement( "div" );
        for ( StackTraceElement stackTraceElement : ex.getStackTrace() ) {
            div.appendElement( "div" ).text( stackTraceElement.toString() );
        }
        return doc;
    }
}
