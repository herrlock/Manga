package de.herrlock.manga.http;

import org.apache.http.protocol.HttpRequestHandler;

public abstract class HttpRequestHandlerWrapper implements HttpRequestHandler {
    private final String pattern;

    public HttpRequestHandlerWrapper( final String pattern ) {
        this.pattern = pattern;
    }

    public final String getPattern() {
        return this.pattern;
    }
}
