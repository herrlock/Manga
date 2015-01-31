package de.herrlock.manga.downloader;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Locale;

import de.herrlock.manga.host.ChapterList;
import de.herrlock.manga.host.ChapterList.Chapter;
import de.herrlock.manga.util.Constants;

public class ChapterListContainer {

    /**
     * the parent-folder to write the pages into
     */
    private File path;
    /**
     * a {@link ChapterList}-Instance containing the {@link URL}s of the {@link Chapter}s
     */
    private ChapterList chapterlist;

    public ChapterListContainer() throws IOException {
        this.chapterlist = ChapterList.getInstance();
        String mangaName = this.chapterlist.getMangaName().toLowerCase( Locale.ENGLISH ).replace( ' ', '_' );
        this.path = new File( Constants.TARGET_FOLDER, mangaName );
    }

    public final File getPath() {
        return this.path;
    }

    public final ChapterList getChapterlist() {
        return this.chapterlist;
    }

    public final int getSize() {
        return this.chapterlist.size();
    }

    public final URL getImageLink( URL pageUrl ) throws IOException {
        return this.chapterlist.imgLink( pageUrl );
    }
}
