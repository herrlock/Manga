package de.herrlock.manga.http;

import de.herrlock.manga.http.location.StopServerLocation;

/**
 * An Exception that is thrown in the {@link StopServerLocation} to indicate the {@link Server} that is should shutdown <br>
 * TODO replace with something that is a little nicer
 * 
 * @author HerrLock
 */
public class CloseServerException extends RuntimeException {
    /**
     * Create a {@link CloseServerException}
     */
    public CloseServerException() {
        super();
    }
}
