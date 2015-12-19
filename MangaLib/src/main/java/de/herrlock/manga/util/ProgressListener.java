package de.herrlock.manga.util;

public interface ProgressListener {
    void progress( int oldProgress, int newProgress, int maxProgress );
}
