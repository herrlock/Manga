package de.herrlock.manga.http.location;

import java.net.URL;

import de.herrlock.manga.http.Response;

/**
 * @author HerrLock
 */
public abstract class Location {
    private final String path;

    public Location( String path ) {
        this.path = path;
    }

    public abstract Response handleXHR( URL url );

    public String getPath() {
        return this.path;
    }
}
