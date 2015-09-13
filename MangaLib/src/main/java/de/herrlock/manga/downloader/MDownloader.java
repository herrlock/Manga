package de.herrlock.manga.downloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Map.Entry;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.downloader.clc.ChapterListContainer;
import de.herrlock.manga.downloader.dqc.DownloadQueueContainer;
import de.herrlock.manga.downloader.pmc.EntryList;
import de.herrlock.manga.downloader.pmc.PictureMapContainer;
import de.herrlock.manga.exceptions.InitializeException;
import de.herrlock.manga.ui.log.LogWindow;
import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * An abstract class to implement downloaders with different behaviour
 * 
 * @author HerrLock
 */
public abstract class MDownloader {
    /**
     * the logger for this class and its subclasses
     */
    protected static final Logger logger = LogManager.getLogger();

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
     * creates a new Downloader. this constructor initializes the ChapterListContainer, the PictureMapContainer and the
     * DownloadQueueContainer
     * 
     * @param conf
     *            the Configuration to use
     */
    public MDownloader( DownloadConfiguration conf ) {
        logger.entry();
        logger.info( conf.getUrl().toExternalForm() );
        try {
            this.clc = new ChapterListContainer( conf );
        } catch ( IOException ex ) {
            throw new InitializeException( ex );
        }
        this.pmc = new PictureMapContainer( this.clc );
        this.dqc = new DownloadQueueContainer( this.clc, conf );
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
        logger.entry();
        EntryList<String, EntryList<Integer, URL>> entries = this.pmc.getEntries();
        entries.sort( entries.getStringComparator( Constants.STRING_NUMBER_COMPARATOR ) );
        LogWindow.setProgress( 0 );
        LogWindow.setProgressMax( entries.size() );
        int progress = 0;
        for ( Entry<String, EntryList<Integer, URL>> entry : entries ) {
            EntryList<Integer, URL> urlMap = entry.getValue();
            String key = entry.getKey();
            downloadChapter( key, urlMap );
            LogWindow.setProgress( ++progress );
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
    private void downloadChapter( String key, EntryList<Integer, URL> entries ) {
        logger.entry( key );
        logger.info( "Download chapter {} ({} pages)", key, entries.size() );
        File chapterFolder = new File( this.clc.getPath(), key );
        if ( chapterFolder.exists() || chapterFolder.mkdirs() ) {
            // add pictures to queue
            this.dqc.addEntryList( chapterFolder, entries );
            // start download
            this.dqc.downloadPages();
        } else {
            throw new RuntimeException( chapterFolder + " does not exists and could not be created" );
        }
        logger.info( "finished chapter {}", key );
    }

}
