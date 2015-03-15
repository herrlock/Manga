package de.herrlock.manga.downloader.clc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;

import de.herrlock.manga.host.ChapterList;
import de.herrlock.manga.host.ChapterList.Chapter;
import de.herrlock.manga.util.Constants;

/**
 * a container-class for the class {@link ChapterList}. also knows the folder where to save the pages into
 * 
 * @author HerrLock
 */
public final class ChapterListContainer {
    /**
     * the parent-folder to write the pages into. set in the constructor to {@code download/<mangaName>_<timestamp>}
     */
    private final File path;
    /**
     * a {@link ChapterList}-Instance containing the {@link URL}s of the {@link Chapter}s
     */
    private final ChapterList chapterlist;

    /**
     * creates a container for the ChapterList
     * 
     * @throws IOException
     * @see ChapterList#getInstance()
     */
    public ChapterListContainer() throws IOException {
        this.chapterlist = ChapterList.getInstance();
        String mangaName = this.chapterlist.getMangaName().toLowerCase( Locale.ENGLISH ).replace( ' ', '_' );
        String timestamp = new SimpleDateFormat( "YYMMddHHmmss", Locale.GERMAN ).format( new Date() );
        this.path = new File( Constants.TARGET_FOLDER, mangaName + "_" + timestamp );
    }

    /**
     * the folder, where the chapters will be stored in.<br>
     * has the name {@code <mangaName>_<timestamp>}
     * 
     * @return the manga-folder
     */
    public File getPath() {
        return this.path;
    }

    /**
     * returns a copy of this ChapterList's elements
     * 
     * @return an array containing all chapters
     */
    public Chapter[] getChapters() {
        return this.chapterlist.toArray( new Chapter[this.chapterlist.size()] );
    }

    /**
     * returns the number of chapters in this container's {@link ChapterList}
     * 
     * @return the size of the chapterlist
     */
    public int getSize() {
        return this.chapterlist.size();
    }

    /**
     * returns the url of the picture on the given chapter
     * 
     * @param pageUrl
     *            the {@link URL} of the (html)-page
     * @return the direct {@link URL} of the picture
     * @throws IOException
     * @xee ChapterList#imgLink(URL)
     */
    public URL getImageLink( URL pageUrl ) throws IOException {
        return this.chapterlist.imgLink( pageUrl );
    }

    public Map<Integer, URL> getAllPageURLs( Chapter chapter ) throws IOException {
        return this.chapterlist.getAllPageURLs( chapter );
    }
}
