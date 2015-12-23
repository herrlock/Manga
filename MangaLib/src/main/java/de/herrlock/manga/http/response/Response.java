package de.herrlock.manga.http.response;

import org.apache.http.HttpEntity;
import org.apache.http.HttpVersion;
import org.apache.http.impl.EnglishReasonPhraseCatalog;
import org.apache.http.message.BasicHttpResponse;
import org.apache.http.message.BasicStatusLine;

/**
 * A Http-Response. Can be subclassed to handle special requirements.
 * 
 * @author HerrLock
 */
public class Response extends BasicHttpResponse {

    /**
     * Creates a Response with given response-code and the given entity
     * 
     * @param httpStatus
     *            the http-responsecode to set
     * @param entity
     *            the entity to set as the Response's content
     */
    public Response( final int httpStatus, final HttpEntity entity ) {
        super( new BasicStatusLine( HttpVersion.HTTP_1_1, httpStatus, null ), EnglishReasonPhraseCatalog.INSTANCE, null );
        setEntity( entity );
    }

}
