package de.herrlock.manga.http.response;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author HerrLock
 */
public class ServerExceptionResponse extends DocumentResponse {

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
