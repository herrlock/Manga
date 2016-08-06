package de.herrlock.manga.downloader;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.downloader.clc.ChapterListContainer;
import de.herrlock.manga.downloader.dqc.DownloadQueueContainer;
import de.herrlock.manga.downloader.pmc.EntryList;
import de.herrlock.manga.downloader.pmc.PictureMapContainer;
import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.ProgressListener;
import de.herrlock.manga.util.Progressable;
import de.herrlock.manga.util.Utils;
import de.herrlock.manga.util.configuration.DownloadConfiguration;
import de.herrlock.manga.util.management.MDownloaderMXBean;

/**
 * An abstract class to implement downloaders with different behaviour
 * 
 * @author HerrLock
 */
public abstract class MDownloader implements Progressable, MDownloaderMXBean {
    private static final Logger logger = LogManager.getLogger();

    private static final AtomicInteger cnt = new AtomicInteger();
    /**
     * contains the {@link ChapterListContainer}
     */
    protected final ChapterListContainer clc;
    /**
     * contains the links to the actual images
     */
    protected final PictureMapContainer pmc;
    /**
     * contains the current queue of downloads
     */
    protected final DownloadQueueContainer dqc;
    /**
     * contains the progress-listeners
     */
    private final List<ProgressListener> progressListeners = new ArrayList<>();
    /**
     * contains the current progress
     */
    private int progress;
    /**
     * contains the maximal progress
     */
    private int maxProgress;
    /**
     * is the downloader started (set in #downloadAll())
     */
    private boolean started;

    /**
     * creates a new Downloader. this constructor initializes the ChapterListContainer, the PictureMapContainer and the
     * DownloadQueueContainer
     * 
     * @param conf
     *            the Configuration to use
     */
    public MDownloader( final DownloadConfiguration conf ) {
        logger.traceEntry( "conf: {}", conf );
        logger.info( conf.getUrl().toExternalForm() );
        this.clc = new ChapterListContainer( conf );
        this.pmc = new PictureMapContainer( this.clc );
        this.maxProgress = this.pmc.getSize();
        this.dqc = new DownloadQueueContainer( this.clc, conf );

        // register MBeans
        int nextCnt = cnt.getAndIncrement();
        String cntString = "type=" + nextCnt;
        Utils.registerMBean( this, "de.herrlock.manga", cntString, "object=MDownloader" );
        // Utils.registerMBean( this.clc, "de.herrlock.manga", cntString, "object=ChapterListContainer" ); // currently no content
        // Utils.registerMBean( this.pmc, "de.herrlock.manga", cntString, "object=PictureMapContainer" ); // currently no content
        Utils.registerMBean( this.dqc, "de.herrlock.manga", cntString, "object=DownloadQueueContainer" );
    }

    /**
     * An abstract method to implement different conditions to start the download. This method should call {@link #downloadAll()}
     */
    protected abstract void run();

    /**
     * returns the number of Chapters in the ChapterListContainer
     * 
     * @return the number of Chapters
     * @see ChapterListContainer#getSize()
     */
    public final int getCLCSize() {
        return this.clc.getSize();
    }

    /**
     * returns the number of Pictures in the PicturesMapContainer
     * 
     * @return the number of Pictures
     * @see PictureMapContainer#getSize()
     */
    public final int getPMCSize() {
        return this.pmc.getSize();
    }

    /**
     * @return the folder where the downloaded chapters are stored in
     * 
     * @see ChapterListContainer#getPath()
     */
    public File getTargetFolder() {
        return this.clc.getPath();
    }

    /**
     * downloads everything in the PictureMapContainer
     */
    protected void downloadAll() {
        logger.traceEntry();
        this.started = true;
        EntryList<String, EntryList<Integer, URL>> entries = this.pmc.getEntries();
        entries.sort( EntryList.getStringComparator( Constants.STRING_NUMBER_COMPARATOR ) );
        setProgress( 0 );
        for ( Entry<String, EntryList<Integer, URL>> entry : entries ) {
            EntryList<Integer, URL> urlMap = entry.getValue();
            String key = entry.getKey();
            downloadChapter( key, urlMap );
            doProgress( entry.getValue().size() );
        }
        logger.info( "Finished successful" );
    }

    /**
     * downloads the Chapter with the "name"of {@code key} and the pictures from {@code urlMap}<br>
     * adds every Chapter to the DownloadQueueContainer and starts the download
     * 
     * @param key
     *            the name of the chapter (in general it is a number as String)
     * @param entries
     *            a map containing the URLs for the pictures
     * @see DownloadQueueContainer#downloadPages()
     */
    private void downloadChapter( final String key, final EntryList<Integer, URL> entries ) {
        logger.traceEntry( "key: {}", key );
        logger.info( "Download chapter {} ({} pages)", key, entries.size() );
        File chapterFolder = new File( getTargetFolder(), key );
        if ( chapterFolder.exists() || chapterFolder.mkdirs() ) {
            // add pictures to queue
            this.dqc.addEntryList( chapterFolder, entries );
            // start download
            this.dqc.downloadPages();
        } else {
            throw new MDRuntimeException( chapterFolder + " does not exists and could not be created" );
        }
        logger.info( "finished chapter {}", key );
    }

    /**
     * increment progress and notify listeners
     * 
     * @param delta
     *            the amount to inrement the progress by
     */
    protected void doProgress( final int delta ) {
        logger.traceEntry( "delta: {}", delta );
        int oldProgress = this.progress;
        setProgress( this.progress + delta );
        for ( ProgressListener listener : this.progressListeners ) {
            listener.progress( oldProgress, this.progress, this.maxProgress );
        }
    }

    @Override
    public boolean getStarted() {
        return this.started;
    }

    @Override
    public void setProgress( final int progress ) {
        this.progress = progress;
    }

    @Override
    public int getProgress() {
        return this.progress;
    }

    @Override
    public void setMaxProgress( final int maxProgress ) {
        this.maxProgress = maxProgress;
    }

    @Override
    public int getMaxProgress() {
        return this.maxProgress;
    }

    @Override
    public void addProgressListener( final ProgressListener listener ) {
        this.progressListeners.add( listener );
    }

    @Override
    public void removeProgressListener( final ProgressListener listener ) {
        this.progressListeners.remove( listener );
    }
}
