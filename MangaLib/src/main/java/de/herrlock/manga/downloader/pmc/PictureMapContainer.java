package de.herrlock.manga.downloader.pmc;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.herrlock.manga.downloader.clc.ChapterListContainer;
import de.herrlock.manga.host.ChapterList.Chapter;
import de.herrlock.manga.util.Utils;

public final class PictureMapContainer {

    /**
     * a {@link Map} containing the {@link URL}s of all the pages
     */
    final Map<String, Map<Integer, URL>> picturemap;
    final ChapterListContainer clc;

    public PictureMapContainer( ChapterListContainer clc ) {
        this.clc = clc;
        int clcSize = clc.getSize();
        this.picturemap = new HashMap<>( clcSize );
        List<Thread> threads = new ArrayList<>( clcSize );
        for ( Chapter chapter : clc.getChapters() ) {
            threads.add( new PictureMapThread( chapter ) );
        }
        Utils.startAndWaitForThreads( threads );
    }

    /**
     * @return a copy of the {@link Map} containing the chapters
     */
    public Map<String, Map<Integer, URL>> getPictureMap() {
        return new HashMap<>( this.picturemap );
    }

    /**
     * returns the summed number of URLs in this container's Map
     * 
     * @return the full number of pages
     */
    public int getSize() {
        int noOfPictures = 0;
        for ( Map<Integer, URL> m : this.picturemap.values() ) {
            noOfPictures += m.size();
        }
        return noOfPictures;
    }

    /**
     * A Thread to add all pages of a chapter to the surrounding PictureMapContainer
     * 
     * @author HerrLock
     */
    private final class PictureMapThread extends Thread {

        private final Chapter chapter;

        public PictureMapThread( Chapter chapter ) {
            this.chapter = chapter;
        }

        @Override
        public void run() {
            Map<Integer, URL> pageMap = getMap();
            PictureMapContainer.this.picturemap.put( this.chapter.getNumber(), pageMap );
            System.out.println( this.chapter );
        }

        /**
         * reads the URLs of the pictures from a chapter
         * 
         * @return a {@link Map} containing the URLs of the pages
         */
        private Map<Integer, URL> getMap() {
            Map<Integer, URL> allPages;
            try {
                allPages = PictureMapContainer.this.clc.getAllPageURLs( this.chapter );
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
