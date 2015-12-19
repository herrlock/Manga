package de.herrlock.manga.util;

public interface Progressable {
    void setProgress( int progress );
    int getProgress();

    void setMaxProgress( int maxProgress );
    int getMaxProgress();

    void addProgressListener( ProgressListener listener );
    void removeProgressListener( ProgressListener listener );
}
