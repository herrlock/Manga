package de.herrlock.manga.util.management;

/**
 * @author HerrLock
 */
public interface MDownloaderMXBean {

    /**
     * @return if the MDownloader was started
     */
    boolean getStarted();

    /**
     * @return the current progress
     */
    int getProgress();

    /**
     * @return the maximum progress
     */
    int getMaxProgress();
}
