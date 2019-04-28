package de.herrlock.manga.http.client;

import java.io.IOException;
import java.net.URL;

import org.eclipse.jetty.client.api.ContentResponse;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.herrlock.manga.exceptions.ResponseHandlerException;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * converts an {@link ContentResponse} to a Jsoup-{@link Document} or retries the HTTP-request
 */
public final class ToDocumentOrRetryHandler extends ResponseHandler<Document> {

    private final URL url;
    private final DownloadConfiguration conf;

    public ToDocumentOrRetryHandler( final URL url, final DownloadConfiguration conf ) {
        this.url = url;
        this.conf = conf;
    }

    @Override
    public Document handle( final ContentResponse response ) {
        if ( response == null ) {
            throw new IllegalArgumentException( "ContentResponse is null" );
        }
        int statusCode = response.getStatus();
        switch ( statusCode ) {
            case 200:
                return response == null ? null : Jsoup.parse( response.getContentAsString() );
            case 503:
                try {
                    Thread.sleep( 1000 );
                } catch ( InterruptedException ex ) {
                    throw new ResponseHandlerException( ex );
                }
                try {
                    return JettyClient.getDataAndExecuteResponseHandler( this.url, this.conf, this );
                } catch ( IOException ex ) {
                    throw new ResponseHandlerException( ex );
                }
            default:
                throw new ResponseHandlerException( "Received non-expected StatusCode: " + statusCode );
        }
    }
}
