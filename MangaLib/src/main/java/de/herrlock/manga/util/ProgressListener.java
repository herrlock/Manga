package de.herrlock.manga.util;

/**
 * An interface for classes that can be informed when the progress of another object has changed.
 * 
 * @author HerrLock
 */
public interface ProgressListener {
    /**
     * @param oldProgress
     *            the previous progress
     * @param newProgress
     *            the new progress
     * @param maxProgress
     *            the highest achievable progress (eg. 100 when percentages are used)
     */
    void progress( int oldProgress, int newProgress, int maxProgress );
}
