package de.herrlock.manga.config.json;

import com.google.gson.GsonBuilder;

/**
 * @author HerrLock
 */
public class Global {

    private String proxy;
    private Integer timeout;
    private Boolean interactive;

    public String getProxy() {
        return this.proxy;
    }

    public void setProxy( final String proxy ) {
        this.proxy = proxy;
    }

    public Boolean getInteractive() {
        return this.interactive;
    }

    public void setInteractive( final Boolean interactive ) {
        this.interactive = interactive;
    }

    public Integer getTimeout() {
        return this.timeout;
    }

    public void setTimeout( final Integer timeout ) {
        this.timeout = timeout;
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson( this );
    }

}
