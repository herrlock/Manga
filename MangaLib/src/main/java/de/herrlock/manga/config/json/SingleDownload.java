package de.herrlock.manga.config.json;

import com.google.gson.GsonBuilder;

/**
 * @author HerrLock
 */
public class SingleDownload {

    private String url;
    private String pattern;

    public String getUrl() {
        return this.url;
    }

    public void setUrl( final String url ) {
        this.url = url;
    }

    public String getPattern() {
        return this.pattern;
    }

    public void setPattern( final String pattern ) {
        this.pattern = pattern;
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson( this );
    }

}
