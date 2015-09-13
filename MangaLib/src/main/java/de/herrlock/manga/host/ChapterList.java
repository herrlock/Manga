package de.herrlock.manga.host;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.herrlock.manga.downloader.pmc.EntryList;
import de.herrlock.manga.exceptions.InitializeException;
import de.herrlock.manga.host.ChapterList.Chapter;
import de.herrlock.manga.util.Utils;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * A class the consists of multiple Chapters
 * 
 * @author HerrLock
 */
public abstract class ChapterList extends ArrayList<Chapter> {
    private static final Logger logger = LogManager.getLogger();

    /**
     * the {@link DownloadConfiguration}-object to use
     */
    protected final DownloadConfiguration conf;

    /**
     * creates an instance of {@linkplain ChapterList}, gets the right {@linkplain Hoster} from the {@linkplain URL} in the given
     * {@link DownloadConfiguration}
     * 
     * @param conf
     *            the {@link DownloadConfiguration} to use
     * @return an instance of {@link ChapterList}; when no suitable Hoster can be detected {@code null}
     * @throws IOException
     *             thrown by {@link Hoster#getChapterList(DownloadConfiguration)}
     */
    public static ChapterList getInstance( DownloadConfiguration conf ) throws IOException {
        logger.entry();
        URL url = conf.getUrl();
        Hoster h = ProvidedHoster.getHostByURL( url );
        if ( h == null ) {
            throw new InitializeException( url + " could not be resolved to a registered host." );
        }
        logger.debug( "Selected Hoster: {}", h.getName() );
        return h.getChapterList( conf );
    }

    /**
     * creates a new ChapterList. reads the ChapterPattern from the central arguments in Utils
     * 
     * @param conf
     *            the {@link DownloadConfiguration} to use
     */
    protected ChapterList( DownloadConfiguration conf ) {
        this.conf = conf;
    }

    /**
     * adds a chapter to this list if the ChapterPattern is null (none defined) or the given number is contained in the
     * ChapterPattern
     * 
     * @param number
     *            the chapter's number
     * @param chapterUrl
     *            the chapter's URL
     */
    protected void addChapter( String number, URL chapterUrl ) {
        if ( this.conf.getPattern().contains( number ) ) {
            super.add( new Chapter( number, chapterUrl ) );
        }
    }

    /**
     * returns the name of the manga. used for the target-folder
     * 
     * @return the manga's name. Not required to be correctly upper-/lowerCase or something like that
     */
    public abstract String getMangaName();

    /**
     * returns the {@link URL} of one page's image
     * 
     * @param url
     *            the URL to the viewpage where the images in shown
     * @return the URL to the image in the given viewpage-URL
     * @throws IOException
     *             in case an IOException occurs
     */
    public abstract URL imgLink( URL url ) throws IOException;

    /**
     * returns a (hopefully soon immutable) {@link EntryList} of all page-URLs
     * 
     * @param url
     *            the url to one chapter
     * @return a {@link EntryList} of all page-urls
     * @throws IOException
     *             in case an IOException occurs
     */
    public EntryList<Integer, URL> getAllPageURLs( URL url ) throws IOException {
        // TODO: make immutable
        return _getAllPageURLs( url );
    }

    /**
     * returns all page-{@link URL}s of one chapter
     * 
     * @param url
     *            the url where to parse the entries from
     * @return an {@link EntryList} containing all page-urls
     * @throws IOException
     *             in case an IOException occurs
     */
    protected abstract EntryList<Integer, URL> _getAllPageURLs( URL url ) throws IOException;

    /**
     * fetches data from the given URL and parses it to a {@link Document}
     * 
     * @param url
     *            the {@link URL} to read from
     * @return a document, parsed from the given URL
     * @throws IOException
     *             in case an IOException occurs
     */
    protected Document getDocument( final URL url ) throws IOException {
        return Utils.getDataAndExecuteResponseHandler( url, this.conf, TO_DOCUMENT_HANDLER );
    }

    /**
     * converts an {@link HttpResponse} to a Jsoup-{@link Document}
     */
    public static final ResponseHandler<Document> TO_DOCUMENT_HANDLER = new ResponseHandler<Document>() {
        @Override
        public Document handleResponse( HttpResponse response ) throws ClientProtocolException, IOException {
            HttpEntity entity = response.getEntity();
            try {
                return Jsoup.parse( EntityUtils.toString( entity, StandardCharsets.UTF_8 ) );
            } finally {
                EntityUtils.consume( entity );
            }
        }
    };

    /**
     * a class to store number and url of a single Chapter
     * 
     * @author HerrLock
     */
    public static class Chapter {
        final String number;
        private final URL chapterUrl;

        Chapter( String number, URL url ) {
            this.number = number;
            this.chapterUrl = url;
        }

        /**
         * Getter for this Chapter's number
         * 
         * @return the number of this Chapter. Might be non-numerical, so it is a String.
         */
        public final String getNumber() {
            return this.number;
        }

        /**
         * Getter for this Chapter's url
         * 
         * @return the {@link URL} of this Chapter. Most times this is the URL of the first page, but the other pages should work
         *         as well.
         */
        public URL getChapterUrl() {
            return this.chapterUrl;
        }

        @Override
        public String toString() {
            return MessageFormat.format( "{0}: {1}", this.number, this.getChapterUrl() );
        }

    }

}
