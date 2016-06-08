package de.herrlock.manga.host;

import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

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
import de.herrlock.manga.downloader.pmc.ImmutableEntryList;
import de.herrlock.manga.exceptions.InitializeException;
import de.herrlock.manga.util.Utils;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * A class the consists of multiple Chapters<br>
 * Implementations require the annotation {@link Details}
 * 
 * @author HerrLock
 */
public abstract class ChapterList implements Iterable<Chapter> {
    private static final Logger logger = LogManager.getLogger();

    /**
     * the {@link DownloadConfiguration}-object to use
     */
    protected final DownloadConfiguration conf;

    private final List<Chapter> list = new ArrayList<>();
    private final Details details;

    /**
     * creates an instance of {@linkplain ChapterList}, gets the right {@linkplain Details} from the {@linkplain URL} in the given
     * {@link DownloadConfiguration}
     * 
     * @param conf
     *            the {@link DownloadConfiguration} to use
     * @return an instance of {@link ChapterList}; when no suitable Hoster can be detected {@code null}
     */
    public static ChapterList getInstance( final DownloadConfiguration conf ) {
        logger.entry();
        URL url = conf.getUrl();
        Hoster h = Hosters.getHostByURL( url );
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
    protected ChapterList( final DownloadConfiguration conf ) {
        this.conf = conf;
        this.details = Objects.requireNonNull( this.getClass().getAnnotation( Details.class ),
            "The class must contain @Details" );
    }

    /**
     * Returns the size of the stored list.
     * 
     * @return the size of the stored list.
     */
    public int size() {
        return this.list.size();
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
    protected void addChapter( final String number, final URL chapterUrl ) {
        if ( this.conf.getPattern().contains( number ) ) {
            this.list.add( new Chapter( number, chapterUrl ) );
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
    public abstract URL imgLink( final URL url ) throws IOException;

    /**
     * returns an immutable {@link EntryList} of all page-URLs. calls {@link #_getAllPageURLs(URL)} and warps the result in an
     * {@link ImmutableEntryList}
     * 
     * @param url
     *            the url to one chapter
     * @return a {@link EntryList} of all page-urls
     * @throws IOException
     *             in case an IOException occurs
     */
    public EntryList<Integer, URL> getAllPageURLs( final URL url ) throws IOException {
        EntryList<Integer, URL> entryList = _getAllPageURLs( url );
        return new ImmutableEntryList<>( entryList );
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
    protected abstract EntryList<Integer, URL> _getAllPageURLs( final URL url ) throws IOException;

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
        public Document handleResponse( final HttpResponse response ) throws ClientProtocolException, IOException {
            HttpEntity entity = response.getEntity();
            try {
                return Jsoup.parse( EntityUtils.toString( entity, StandardCharsets.UTF_8 ) );
            } finally {
                EntityUtils.consume( entity );
            }
        }
    };

    @Override
    public Iterator<Chapter> iterator() {
        ArrayList<Chapter> arrayList = new ArrayList<>( this.list );
        if ( this.details.reversed() ) {
            Collections.reverse( arrayList );
        }
        return arrayList.iterator();
    }

}
