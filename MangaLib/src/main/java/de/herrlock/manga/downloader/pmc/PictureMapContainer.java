package de.herrlock.manga.downloader.pmc;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.downloader.clc.ChapterListContainer;
import de.herrlock.manga.host.ChapterList.Chapter;
import de.herrlock.manga.util.Utils;

public final class PictureMapContainer {
    static final Logger logger = LogManager.getLogger();

    /**
     * a {@link Map} containing the {@link URL}s of all the pages
     */
    private final EntryList<String, EntryList<Integer, URL>> entries;
    private final ChapterListContainer clc;

    public PictureMapContainer( ChapterListContainer clc ) {
        this.clc = clc;
        int clcSize = clc.getSize();
        this.entries = new EntryList<>( clcSize );
        List<PictureMapThread> callables = new ArrayList<>( clcSize );
        for ( Chapter chapter : clc.getChapters() ) {
            callables.add( new PictureMapThread( chapter ) );
        }
        Utils.callCallables( callables );
    }

    /**
     * @return a copy of the {@link Map} containing the chapters
     */
    public EntryList<String, EntryList<Integer, URL>> getEntries() {
        return new EntryList<>( this.entries );
    }

    /**
     * returns the summed number of URLs in this container's Map
     * 
     * @return the full number of pages
     */
    public int getSize() {
        int noOfPictures = 0;
        for ( Entry<String, EntryList<Integer, URL>> m : this.entries ) {
            noOfPictures += m.getValue().size();
        }
        return noOfPictures;
    }

    void addEntry( String number, EntryList<Integer, URL> pageMap ) {
        this.entries.addEntry( number, pageMap );
    }

    EntryList<Integer, URL> getAllPageURLs( Chapter chapter ) throws IOException {
        return this.clc.getAllPageURLs( chapter );
    }

    /**
     * A Thread to add all pages of a chapter to the surrounding PictureMapContainer
     * 
     * @author HerrLock
     */
    private final class PictureMapThread implements Callable<Void> {

        private final Chapter chapter;

        public PictureMapThread( Chapter chapter ) {
            this.chapter = chapter;
        }

        @Override
        public Void call() {
            EntryList<Integer, URL> pageMap = getMap();
            PictureMapContainer.this.addEntry( this.chapter.getNumber(), pageMap );
            logger.info( this.chapter.toString() );
            return null;
        }

        /**
         * reads the URLs of the pictures from a chapter
         * 
         * @return a {@link Map} containing the URLs of the pages
         */
        private EntryList<Integer, URL> getMap() {
            EntryList<Integer, URL> allPages;
            try {
                allPages = PictureMapContainer.this.getAllPageURLs( this.chapter );
            } catch ( SocketTimeoutException stex ) {
                System.out.println( "read timed out (chapter " + this.chapter.getNumber() + "), trying again" );
                allPages = getMap();
            } catch ( IOException ioex ) {
                if ( ioex.getMessage().contains( "503" ) ) {
                    // http-statuscode 503
                    System.out.println( "HTTP-Status 503 (chapter " + this.chapter.getNumber() + "), trying again" );
                    allPages = getMap();
                } else {
                    throw new RuntimeException( ioex );
                }
            }
            return allPages;
        }

    }
}
