package de.herrlock.manga.http.response;

import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.jsoup.nodes.Document;

/**
 * A {@link Response} that results in a Jsoup-{@link Document}
 * 
 * @author HerrLock
 */
public class DocumentResponse extends Response {

    /**
     * Creates a DocumentResponse with the given response-code and the given document
     * 
     * @param code
     *            the response-code
     * @param document
     *            the response-document
     */
    public DocumentResponse( final int httpStatus, final Document document ) {
        super( httpStatus, new StringEntity( document.toString(), ContentType.TEXT_HTML ) );
    }

}
