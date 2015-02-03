package de.herrlock.manga.downloader;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.herrlock.manga.host.ChapterList;
import de.herrlock.manga.host.ChapterList.Chapter;

public class PictureMapContainer {

    /**
     * a {@link Map} containing the {@link URL}s of all the pages
     */
    private Map<String, Map<Integer, URL>> picturemap;

    public PictureMapContainer( ChapterListContainer clc ) throws IOException {
        ChapterList chapterlist = clc.getChapterlist();
        if ( chapterlist != null ) {
            this.picturemap = new HashMap<>( chapterlist.size() );
            for ( Chapter chapter : chapterlist ) {
                System.out.println( chapter );
                Map<Integer, URL> pageMap = chapterlist.getAllPageURLs( chapter );
                this.picturemap.put( chapter.getNumber(), pageMap );
            }
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

    public final Map<Integer, URL> getUrlMap( String key ) {
        return this.picturemap.get( key );
    }

}
