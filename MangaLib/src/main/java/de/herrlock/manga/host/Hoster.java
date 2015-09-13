package de.herrlock.manga.host;

import java.io.IOException;
import java.net.URL;

import de.herrlock.manga.util.configuration.DownloadConfiguration;

public interface Hoster {

    /**
     * Getter for the Hoster's URL
     * 
     * @return the base-url of the Hoster
     */
    URL getUrl();

    /**
     * Getter for the Hoster's name
     * 
     * @return the name of the Hoster
     */
    String getName();

    /**
     * creates an instance of a Host, specified by the URL's host-part
     * 
     * @param conf
     *            the {@link DownloadConfiguration} from the downloader
     * @return an instance of the ChapterList specified by the current {@link HosterList}-Object
     * @throws IOException
     *             thrown by the constructors of the special ChapterList-implementations
     */
    ChapterList getChapterList( DownloadConfiguration conf ) throws IOException;

}
