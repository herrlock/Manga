package de.herrlock.manga.http.location;

import java.net.MalformedURLException;
import java.net.URL;

import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.RequestLine;
import org.apache.http.protocol.HttpContext;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.http.HttpRequestHandlerWrapper;
import de.herrlock.manga.http.response.Response;
import de.herrlock.manga.http.response.ServerExceptionResponse;

/**
 * A base-class for Locations that can be registered at a {@link de.herrlock.manga.http.Server}
 * 
 * @author HerrLock
 */
public abstract class Location extends HttpRequestHandlerWrapper {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Create a new Location
     * 
     * @param path
     *            the path from where to request data, must represent the path-part from a {@link URL}
     * @see URL#getPath()
     */
    public Location( final String path ) {
        super( path );
    }

    /**
     * handles an XMLHttpRequest
     * 
     * @param url
     *            the requested {@link URL}
     * @return the Response created
     */
    protected abstract Response handleXHR( final URL url );

    @Override
    public void handle( final HttpRequest request, final HttpResponse response, final HttpContext context ) {
        logger.debug( "handler; request: {}", request );
        Response res;
        try {
            RequestLine requestLine = request.getRequestLine();
            String protocol = requestLine.getProtocolVersion().getProtocol();
            String host = request.getFirstHeader( "Host" ).getValue();
            URL urlContext = new URL( protocol + "://" + host );
            String uri = requestLine.getUri();
            URL url = new URL( urlContext, uri );
            res = handleXHR( url );
        } catch ( MalformedURLException ex ) {
            res = new ServerExceptionResponse( ex );
        }
        logger.debug( "handler; response: {}", response );
        response.setStatusLine( res.getStatusLine() );
        response.setHeaders( res.getAllHeaders() );
        response.setEntity( res.getEntity() );
        logger.debug( "handler; response: {}", response );
    }
}
