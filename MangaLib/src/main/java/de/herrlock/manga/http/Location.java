package de.herrlock.manga.http;

import java.net.URL;

/**
 * @author HerrLock
 */
public abstract class Location {
    private String path;

    public Location( String path ) {
        this.path = path;
    }

    public abstract Response handleXHR( URL url );

    public String getPath() {
        return this.path;
    }
}
