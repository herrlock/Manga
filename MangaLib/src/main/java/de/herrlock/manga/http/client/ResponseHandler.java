package de.herrlock.manga.http.client;

import java.io.IOException;
import java.util.function.Function;

import org.eclipse.jetty.client.api.ContentResponse;

import de.herrlock.manga.exceptions.ResponseHandlerException;

public abstract class ResponseHandler<T> implements Function<ContentResponse, T> {

    protected abstract T handle( ContentResponse input ) throws IOException;

    @Override
    public final T apply( final ContentResponse input ) throws ResponseHandlerException {
        try {
            return handle( input );
        } catch ( IOException ex ) {
            throw new ResponseHandlerException( ex );
        }
    }

    public static <T> ResponseHandler<T> fromFunction( final Function<ContentResponse, T> delegate ) {
        return new DelegateResponseHandler<>( delegate );
    }

    private static class DelegateResponseHandler<T> extends ResponseHandler<T> {
        private final Function<ContentResponse, T> delegate;

        public DelegateResponseHandler( final Function<ContentResponse, T> delegate ) {
            this.delegate = delegate;
        }

        @Override
        protected T handle( final ContentResponse input ) throws IOException {
            return delegate.apply( input );
        }
    }
}
