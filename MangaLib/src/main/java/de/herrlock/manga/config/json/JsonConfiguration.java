package de.herrlock.manga.config.json;

import java.util.List;

import com.google.gson.GsonBuilder;

/**
 * @author HerrLock
 */
public class JsonConfiguration {

    private Global global;
    private List<SingleDownload> downloads;

    public Global getGlobal() {
        return this.global;
    }

    public void setGlobal( final Global global ) {
        this.global = global;
    }

    public List<SingleDownload> getDownloads() {
        return this.downloads;
    }

    public void setDownloads( final List<SingleDownload> downloads ) {
        this.downloads = downloads;
    }

    @Override
    public String toString() {
        return new GsonBuilder().setPrettyPrinting().create().toJson( this );
    }

}
