package de.herrlock.manga.downloader;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

class DownloadQueueContainer {

    private final List<Page> dlQueue = new ArrayList<>();

    public void add( Page p ) {
        this.dlQueue.add( p );
    }

    public boolean isEmpty() {
        return this.dlQueue.isEmpty();
    }

    /**
     * creates a new {@linkplain Collections#unmodifiableList(List) unmodifiable List} containing the current elements of this
     * list.<br>
     * {@linkplain List#clear() clear}s this list afterwards
     * 
     * @return a new list with the elements of this list
     */
    public List<Page> getNewList() {
        List<Page> list = Collections.unmodifiableList( new ArrayList<>( this.dlQueue ) );
        this.dlQueue.clear();
        return list;
    }
}
