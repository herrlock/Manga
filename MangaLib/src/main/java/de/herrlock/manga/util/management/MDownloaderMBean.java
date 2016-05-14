package de.herrlock.manga.util.management;

public interface MDownloaderMBean {
    boolean getStarted();
    int getProgress();
    int getMaxProgress();
}
