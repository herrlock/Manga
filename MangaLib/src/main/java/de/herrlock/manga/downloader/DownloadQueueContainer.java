package de.herrlock.manga.downloader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DownloadQueueContainer {

    private final List<Page> dlQueue = new ArrayList<>();

    public void add( Page p ) {
        this.dlQueue.add( p );
    }

    public void clear() {
        this.dlQueue.clear();
    }

    public boolean isEmpty() {
        return this.dlQueue.isEmpty();
    }

    public List<Page> getList() {
        return Collections.unmodifiableList( new ArrayList<>( this.dlQueue ) );
    }
}
