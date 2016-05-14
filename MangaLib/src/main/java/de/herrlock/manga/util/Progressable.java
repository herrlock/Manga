package de.herrlock.manga.util;

/**
 * An interface for a class that contains a progress. The form as Interface is used to make sure the using class can still extend
 * another class. Typically this Interface is implemented following:
 * 
 * <pre>
 * <code>
    public class Foo implements Progressable {
        private int progress;
        private int maxProgress;
        private Collection&lt;ProgressListener&gt; listeners = new HashSet&lt;&gt;(); // your favourite Collection
    
        &#64;Override
        public void setProgress( int progress ) {
            this.progress = progress;
        }
        &#64;Override
        public int getProgress() {
            return this.progress;
        }
        &#64;Override
        public void setMaxProgress( int maxProgress ) {
            this.maxProgress = maxProgress;
        }
        &#64;Override
        public int getMaxProgress() {
            return this.maxProgress;
        }
        &#64;Override
        public void addProgressListener( ProgressListener listener ) {
            this.listeners.add( listener );
        }
        &#64;Override
        public void removeProgressListener( ProgressListener listener ) {
            this.listeners.remove( listener );
        }
    }
 * </code>
 * </pre>
 * 
 * @author HerrLock
 */
public interface Progressable {
    /**
     * @param progress
     *            the new progress
     */
    void setProgress( int progress );
    /**
     * @return the current progress
     */
    int getProgress();

    /**
     * @param maxProgress
     *            the new maximal progress
     */
    void setMaxProgress( int maxProgress );
    /**
     * @return the maximal progress
     */
    int getMaxProgress();

    /**
     * @param listener
     *            the listener to add
     */
    void addProgressListener( ProgressListener listener );
    /**
     * @param listener
     *            the listener to remove
     */
    void removeProgressListener( ProgressListener listener );
}
