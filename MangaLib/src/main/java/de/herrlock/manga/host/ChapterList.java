package de.herrlock.manga.host;

import java.io.IOException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.herrlock.manga.exceptions.InitializeException;
import de.herrlock.manga.host.ChapterList.Chapter;
import de.herrlock.manga.util.ChapterPattern;
import de.herrlock.manga.util.Utils;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * A class the consists of multiple Chapters
 * 
 * @author HerrLock
 */
public abstract class ChapterList extends ArrayList<Chapter> {
    protected static final Logger logger = LogManager.getLogger();

    private final ChapterPattern cp;
    protected final DownloadConfiguration conf;

    /**
     * creates an instance of {@linkplain ChapterList}, gets the right {@linkplain Hoster} from the {@linkplain URL} in
     * {@code Utils.arguments}
     * 
     * @return an instance of {@link ChapterList}; when no suitable Hoster can be detected {@code null}
     * @throws IOException
     *             thrown by {@link Hoster#getChapterList(URL)}
     */
    public static ChapterList getInstance( DownloadConfiguration conf ) throws IOException {
        logger.entry();
        URL url = conf.getUrl();
        Hoster h = Hoster.getHostByURL( url );
        if ( h == null ) {
            throw new InitializeException( url + " could not be resolved to a registered host." );
        }
        logger.debug( "Selected Hoster: {}", h.getName() );
        return h.getChapterList( conf );
    }

    /**
     * creates a new ChapterList. reads the ChapterPattern from the central arguments in Utils
     */
    protected ChapterList( DownloadConfiguration conf ) {
        this.conf = conf;
        this.cp = conf.getPattern();
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
        if ( this.cp.contains( number ) ) {
            super.add( new Chapter( number, chapterUrl ) );
        }
    }

    /**
     * returns the name of the manga. used for the target-folder
     */
    public abstract String getMangaName();

    /**
     * returns the {@link URL} of one page's image
     */
    public abstract URL imgLink( URL url ) throws IOException;

    public Map<Integer, URL> getAllPageURLs( URL url ) throws IOException {
        return Collections.unmodifiableMap( _getAllPageURLs( url ) );
    }

    /**
     * returns all page-{@link URL}s of one chapter
     */
    protected abstract Map<Integer, URL> _getAllPageURLs( URL url ) throws IOException;

    /**
     * gets all URLs for one Chapter
     */
    public Map<Integer, URL> getAllPageURLs( Chapter c ) throws IOException {
        return getAllPageURLs( c.chapterUrl );
    }

    /**
     * fetches data from the given URL and parses it to a {@link Document}
     * 
     * @param url
     *            the {@link URL} to read from
     * @return a document, parsed from the given URL
     * @throws IOException
     */
    protected Document getDocument( final URL url ) throws IOException {
        return Utils.getDataAndExecuteResponseHandler( url, this.conf, TO_DOCUMENT_HANDLER );
    }

    public static final ResponseHandler<Document> TO_DOCUMENT_HANDLER = new ResponseHandler<Document>() {
        @Override
        public Document handleResponse( HttpResponse response ) throws ClientProtocolException, IOException {
            String result = Utils.TO_STRING_HANDLER.handleResponse( response );
            return Jsoup.parse( result );
        }
    };

    /**
     * a class to
     * 
     * @author HerrLock
     */
    public static class Chapter {
        final String number;
        final URL chapterUrl;

        Chapter( String number, URL url ) {
            this.number = number;
            this.chapterUrl = url;
        }

        public final String getNumber() {
            return this.number;
        }

        @Override
        public String toString() {
            return MessageFormat.format( "{0}: {1}", this.number, this.chapterUrl );
        }
    }

}
