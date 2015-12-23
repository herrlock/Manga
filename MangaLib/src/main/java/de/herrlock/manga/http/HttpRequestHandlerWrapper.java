package de.herrlock.manga.http;

import org.apache.http.protocol.HttpRequestHandler;

/**
 * An implementation of a {@link HttpRequestHandler} that contains its url-pattern.
 * 
 * @author HerrLock
 */
public abstract class HttpRequestHandlerWrapper implements HttpRequestHandler {
    private final String pattern;

    /**
     * Creates a new {@linkplain HttpRequestHandlerWrapper} with the given pattern.
     * 
     * @param pattern
     *            the pattern to be passed to the Server
     * 
     * @see org.apache.http.impl.bootstrap.ServerBootstrap#registerHandler(String, HttpRequestHandler)
     */
    public HttpRequestHandlerWrapper( final String pattern ) {
        this.pattern = pattern;
    }

    /**
     * returns this object's pattern
     * 
     * @return the pattern registered with this object
     */
    public final String getPattern() {
        return this.pattern;
    }
}
