package de.herrlock.manga.downloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.herrlock.manga.downloader.clc.ChapterListContainer;
import de.herrlock.manga.downloader.dqc.DownloadQueueContainer;
import de.herrlock.manga.downloader.pmc.PictureMapContainer;
import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.Utils;

public abstract class MDownloader extends Thread {

    protected final ChapterListContainer clc;
    protected PictureMapContainer pmc;
    protected final DownloadQueueContainer dqc;

    /**
     * creates a new Downloader. this constructor initializes the ChapterListContainer and the DownloadQueueContainer
     * 
     * @param p
     *            the Properties to work with
     */
    public MDownloader( Properties p ) {
        Utils.setArguments( p );
        System.out.println( Utils.getMangaURL() );
        try {
            this.clc = new ChapterListContainer();
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
        this.dqc = new DownloadQueueContainer( this.clc );
    }

    @Override
    public void run() {
        try {
            runX();
        } catch ( RuntimeException ex ) {
            System.out.println( ex.getMessage() );
            throw ex;
        } catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            throw new RuntimeException( ex );
        }
    }

    protected abstract void runX();

    /**
     * initializese the PictureMapContainer with the ChapterListContainer
     */
    public void initPMC() {
        Utils.trace( "initPMC()" );
        System.out.println( "checking chapters for number of pages" );
        this.pmc = new PictureMapContainer( this.clc );
    }

    /**
     * returns the number of Chapters in the ChapterListContainer
     * 
     * @return the number of Chapters
     * @see ChapterListContainer#getSize()
     */
    public int getCLCSize() {
        return this.clc.getSize();
    }

    /**
     * returns the number of Pictures in the PicturesMapContainer
     * 
     * @return the number of Pictures or 0, if the PMC has not been initialized
     * @see #initPMC()
     * @see PictureMapContainer#getSize()
     */
    public int getPMCSize() {
        return this.pmc != null ? this.pmc.getSize() : 0;
    }

    /**
     * downloads everything in the PictureMapContainer<br>
     * basically calls {@link #downloadChapter(String, Map)} for every chapter
     */
    public void downloadAll() {
        Utils.trace( "downloadAll()" );
        if ( this.pmc != null ) {
            Map<String, Map<Integer, URL>> picturemap = this.pmc.getPictureMap();
            if ( picturemap != null ) {
                List<String> keys = new ArrayList<>( picturemap.keySet() );
                Collections.sort( keys, Constants.STRING_NUMBER_COMPARATOR );
                for ( String key : keys ) {
                    downloadChapter( key, picturemap.get( key ) );
                }
            } else {
                throw new RuntimeException( "PageMap not initialized" );
            }
        } else {
            Utils.trace( "pmc == null" );
            System.out.println( "pmc == null" );
        }
    }

    /**
     * downloads the Chapter with the "name"of {@code key} and the pictures from {@code urlMap}<br>
     * adds every Chapter th the DownloadQueueContainer and starts the download
     * 
     * @param key
     *            the name of the chapter (in general it is a number as String)
     * @param urlMap
     *            a map containing the URLs for the pictures
     * @see DownloadQueueContainer#downloadPages()
     */
    private void downloadChapter( String key, Map<Integer, URL> urlMap ) {
        Utils.trace( "downloadChapter( " + key + " )" );
        if ( this.clc != null ) {
            System.out.println( "Download chapter " + key + " (" + urlMap.size() + " pages)" );
            File chapterFolder = new File( this.clc.getPath(), key );
            if ( chapterFolder.exists() || chapterFolder.mkdirs() ) {
                // add pictures to queue
                for ( Map.Entry<Integer, URL> e : urlMap.entrySet() ) {
                    this.dqc.add( e.getValue(), chapterFolder, e.getKey().intValue() );
                }
                // start download
                this.dqc.downloadPages();
            } else {
                throw new RuntimeException( chapterFolder + "does not exists and could not be created" );
            }
            System.out.println( "finished chapter " + key + "\n" );
        } else {
            Utils.trace( "clc == null" );
            System.out.println( "clc == null" );
        }
    }

}
