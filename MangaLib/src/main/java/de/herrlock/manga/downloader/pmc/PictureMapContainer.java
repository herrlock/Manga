package de.herrlock.manga.downloader.pmc;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.herrlock.manga.downloader.clc.ChapterListContainer;
import de.herrlock.manga.host.ChapterList;
import de.herrlock.manga.host.ChapterList.Chapter;
import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.Utils;

public final class PictureMapContainer {

    /**
     * a {@link Map} containing the {@link URL}s of all the pages
     */
    Map<String, Map<Integer, URL>> picturemap;

    public PictureMapContainer( ChapterListContainer clc ) {
        ChapterList chapterlist = clc.getChapterlist();
        if ( chapterlist != null ) {
            this.picturemap = new HashMap<>( chapterlist.size() );
            List<Thread> threads = new ArrayList<>( chapterlist.size() );
            for ( Chapter chapter : chapterlist ) {
                threads.add( new PictureMapThread( chapterlist, chapter ) );
            }
            Utils.startAndWaitForThreads( threads );
        } else {
            throw new RuntimeException( "ChapterList not initialized" );
        }
    }

    public final Map<String, Map<Integer, URL>> getPictureMap() {
        return this.picturemap;
    }

    public final int getSize() {
        int noOfPictures = 0;
        for ( Map<Integer, URL> m : this.picturemap.values() ) {
            noOfPictures += m.size();
        }
        return noOfPictures;
    }

    private class PictureMapThread extends Thread {

        private ChapterList chapterlist;
        private Chapter chapter;

        public PictureMapThread( ChapterList chapterlist, Chapter chapter ) {
            this.chapterlist = chapterlist;
            this.chapter = chapter;
        }

        @Override
        public void run() {
            Map<Integer, URL> pageMap = getMap();
            PictureMapContainer.this.picturemap.put( this.chapter.getNumber(), pageMap );
            System.out.println( this.chapter );
        }

        private Map<Integer, URL> getMap() {
            try {
                Map<Integer, URL> pageMap = this.chapterlist.getAllPageURLs( this.chapter );
                return pageMap;
            } catch ( SocketTimeoutException stex ) {
                try {
                    sleep( Constants.PARAM_TIMEOUT_DEFAULT );
                    return getMap();
                } catch ( InterruptedException iex ) {
                    throw new RuntimeException( iex );
                }
            } catch ( IOException ioex ) {
                throw new RuntimeException( ioex );
            }
        }

    }
}
