package de.herrlock.manga.http.location;

import java.net.URL;

import de.herrlock.manga.http.Server;
import de.herrlock.manga.http.response.Response;

/**
 * A base-class for Locations that can be registered at a {@link Server}
 * 
 * @author HerrLock
 */
public abstract class Location {
    private final String path;

    /**
     * Create a new Location
     * 
     * @param path
     *            the path from where to request data, must represent the path-part from a {@link URL}
     * @see URL#getPath()
     */
    public Location( final String path ) {
        this.path = path;
    }

    /**
     * handles an XMLHttpRequest
     * 
     * @param url
     *            the requested {@link URL}
     * @return the Response created
     */
    public abstract Response handleXHR( final URL url );

    /**
     * Getter for the Location's path
     * 
     * @return the path this Location is registered with
     */
    public String getPath() {
        return this.path;
    }
}
