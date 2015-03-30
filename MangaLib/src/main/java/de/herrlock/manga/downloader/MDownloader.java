package de.herrlock.manga.downloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import de.herrlock.manga.downloader.clc.ChapterListContainer;
import de.herrlock.manga.downloader.dqc.DownloadQueueContainer;
import de.herrlock.manga.downloader.pmc.PictureMapContainer;
import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.Utils;

public abstract class MDownloader {

    protected final ChapterListContainer clc;
    protected final PictureMapContainer pmc;
    protected final DownloadQueueContainer dqc;

    /**
     * creates a new Downloader. this constructor initializes the ChapterListContainer, the PictureMapContainer and the
     * DownloadQueueContainer
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
        this.pmc = new PictureMapContainer( this.clc );
        this.dqc = new DownloadQueueContainer( this.clc );
    }

    protected abstract void run();

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
     * @return the number of Pictures
     * @see PictureMapContainer#getSize()
     */
    public int getPMCSize() {
        return this.pmc.getSize();
    }

    /**
     * downloads everything in the PictureMapContainer<br>
     * basically calls {@link #downloadChapter(String, Map)} for every chapter
     */
    public void downloadAll() {
        Utils.trace( "downloadAll()" );
        Map<String, Map<Integer, URL>> picturemap = this.pmc.getPictureMap();
        List<String> keys = new ArrayList<>( picturemap.keySet() );
        Collections.sort( keys, Constants.STRING_NUMBER_COMPARATOR );
        for ( String key : keys ) {
            downloadChapter( key, picturemap.get( key ) );
            picturemap.remove( key );
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
        System.out.println( "Download chapter " + key + " (" + urlMap.size() + " pages)" );
        File chapterFolder = new File( this.clc.getPath(), key );
        if ( chapterFolder.exists() || chapterFolder.mkdirs() ) {
            // add pictures to queue
            Set<Map.Entry<Integer, URL>> entrySet = urlMap.entrySet();
            this.dqc.addEntrySet( chapterFolder, entrySet );
            // start download
            this.dqc.downloadPages();
        } else {
            throw new RuntimeException( chapterFolder + "does not exists and could not be created" );
        }
        System.out.println( "finished chapter " + key + "\n" );
    }

    /**
     * returns the folder where the downloaded chapters are stored in
     * 
     * @see ChapterListContainer#getPath()
     */
    public File getTargetFolder() {
        return this.clc.getPath();
    }

}
