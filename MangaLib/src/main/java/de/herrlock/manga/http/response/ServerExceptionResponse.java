package de.herrlock.manga.http.response;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import de.herrlock.manga.http.Server;

/**
 * A {@link Response} that indiates an Exception on the {@link Server}
 * 
 * @author HerrLock
 */
public class ServerExceptionResponse extends DocumentResponse {

    /**
     * Creates a {@link ServerExceptionResponse} with the give cause
     * 
     * @param ex
     *            the cause of this Response
     */
    public ServerExceptionResponse( Exception ex ) {
        super.setDocument( createDocument( ex ) );
        super.setCode( 500 );
    }

    private Document createDocument( Exception ex ) {
        Document doc = Document.createShell( "http://localhost" );
        doc.appendElement( "head" ).appendElement( "title" ).text( "Internal Error" );
        Element div = doc.appendElement( "body" ).appendElement( "div" );
        for ( StackTraceElement stackTraceElement : ex.getStackTrace() ) {
            div.appendElement( "div" ).text( stackTraceElement.toString() );
        }
        return doc;
    }
}
