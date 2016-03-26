package de.herrlock.manga.downloader.clc;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import de.herrlock.manga.downloader.pmc.EntryList;
import de.herrlock.manga.host.Chapter;
import de.herrlock.manga.host.ChapterList;
import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * a container-class for the class {@link ChapterList}. also knows the folder where to save the pages into
 * 
 * @author HerrLock
 */
public final class ChapterListContainer implements Iterable<Chapter> {
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
     * @param conf
     *            the {@link DownloadConfiguration} to use
     * @throws IOException
     *             thrown by {@link ChapterList#getInstance(DownloadConfiguration)}
     * @see ChapterList#getInstance(DownloadConfiguration)
     */
    public ChapterListContainer( final DownloadConfiguration conf ) throws IOException {
        this.chapterlist = ChapterList.getInstance( conf );
        String mangaName = this.chapterlist.getMangaName().toLowerCase( Locale.ENGLISH ).replace( ' ', '_' );
        String timestamp = new SimpleDateFormat( "YYMMddHHmmss", Locale.GERMAN ).format( new Date() );
        String mangaNameEscaped = mangaName.replaceAll( "[\\/:*?<>\"|]", "_" );
        this.path = new File( Constants.TARGET_FOLDER, mangaNameEscaped + "_" + timestamp );
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
     * returns an Iterator of this ChapterList's elements
     * 
     * @return an Iterator containing all chapters
     */
    @Override
    public Iterator<Chapter> iterator() {
        return this.chapterlist.iterator();
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
     *             thrown by {@link ChapterList#imgLink(URL)}
     * @see ChapterList#imgLink(URL)
     */
    public URL getImageLink( final URL pageUrl ) throws IOException {
        return this.chapterlist.imgLink( pageUrl );
    }

    /**
     * 
     * @param chapter
     *            the chapter to get the URLs for
     * @return an {@link EntryList} containing all URLs
     * @throws IOException
     *             thrown by {@link ChapterList#getAllPageURLs(URL)}
     */
    public EntryList<Integer, URL> getAllPageURLs( final Chapter chapter ) throws IOException {
        return this.chapterlist.getAllPageURLs( chapter.getChapterUrl() );
    }
}
