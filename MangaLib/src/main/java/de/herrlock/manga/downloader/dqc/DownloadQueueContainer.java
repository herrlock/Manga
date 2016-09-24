package de.herrlock.manga.downloader.dqc;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayDeque;
import java.util.Collection;
import java.util.List;
import java.util.Map.Entry;
import java.util.Queue;
import java.util.concurrent.Callable;

import org.apache.http.HttpEntity;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.impl.client.AbstractResponseHandler;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.ImmutableList;
import com.google.common.io.ByteStreams;

import de.herrlock.manga.downloader.clc.ChapterListContainer;
import de.herrlock.manga.downloader.pmc.EntryList;
import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.util.Utils;
import de.herrlock.manga.util.configuration.DownloadConfiguration;
import de.herrlock.manga.util.management.DownloadQueueContainerMXBean;

/**
 * A container for a list of downloads
 * 
 * @author HerrLock
 */
public final class DownloadQueueContainer implements DownloadQueueContainerMXBean {
    static final Logger logger = LogManager.getLogger();

    private final Collection<Page> dlQueue = new ArrayDeque<>();
    private final ChapterListContainer clc;
    final DownloadConfiguration conf;

    /**
     * @param clc
     *            the container with the list of chapters
     * @param conf
     *            the configuration to use
     */
    public DownloadQueueContainer( final ChapterListContainer clc, final DownloadConfiguration conf ) {
        this.clc = clc;
        this.conf = conf;
    }

    @Override
    public int getSize() {
        return this.dlQueue.size();
    }

    /**
     * adds a page to thie queue
     * 
     * @param p
     *            the page to add
     */
    public void add( final Page p ) {
        this.dlQueue.add( p );
    }

    /**
     * adds a new page with the given parameters to the queue
     * 
     * @param pageUrl
     *            the page's URL
     * @param chapterFolder
     *            the folder to store the page in
     * @param pageNumber
     *            the number of the page
     * 
     */
    public void add( final URL pageUrl, final File chapterFolder, final int pageNumber ) {
        this.add( new Page( pageUrl, chapterFolder, pageNumber ) );
    }

    /**
     * adds a {@link Collection} of Integer-URL-Pairs to this Container
     * 
     * @param chapterFolder
     *            the folder to store the pages in
     * @param entryList
     *            the list of entries
     * 
     */
    public void addEntryList( final File chapterFolder, final EntryList<Integer, URL> entryList ) {
        for ( Entry<Integer, URL> e : entryList ) {
            this.add( e.getValue(), chapterFolder, e.getKey().intValue() );
        }
    }

    /**
     * downloads all pages in the queue.<br>
     * clears the queue, calls itself in case a download timed out
     */
    public void downloadPages() {
        logger.traceEntry();
        List<Page> pages = ImmutableList.copyOf( this.dlQueue );
        this.dlQueue.clear();
        // download pictures from the ChapterListContainer
        Queue<DownloadThread> callables = new ArrayDeque<>( pages.size() );
        for ( Page p : pages ) {
            DownloadThread downloadThread = new DownloadThread( p );
            callables.add( downloadThread );
        }
        // waits for the Callables to complete
        Utils.callCallables( callables );
        // all Callables are finished here
        if ( !this.dlQueue.isEmpty() ) {
            downloadPages();
        }
    }

    URL getImageLink( final URL pageUrl ) throws IOException {
        return this.clc.getImageLink( pageUrl );
    }

    void executeDownload( final URL url, final ResponseHandler<?> handler ) throws ClientProtocolException, IOException {
        Utils.getDataAndExecuteResponseHandler( url, this.conf, handler );
    }

    /**
     * A Thread to download one image
     * 
     * @author HerrLock
     */
    private final class DownloadThread extends AbstractResponseHandler<Void> implements Callable<Void> {
        private final Page page;

        public DownloadThread( final Page page ) {
            logger.traceEntry( "DownloadThread - page-url: {}", page.getUrl() );
            this.page = page;
        }

        @Override
        public Void handleEntity( final HttpEntity entity ) throws IOException {
            try ( InputStream in = entity.getContent() ) {
                try ( OutputStream out = new FileOutputStream( this.page.getTargetFile() ) ) {
                    ByteStreams.copy( in, out );
                }
            } finally {
                EntityUtils.consume( entity );
            }
            return null;
        }

        /**
         * downloads the page
         */
        @Override
        public Void call() {
            URL pageUrl = this.page.getUrl();
            try {
                URL imageURL = getImageLink( pageUrl );
                logger.debug( "start reading image {}", imageURL );
                executeDownload( imageURL, this );
                logger.debug( "saved image to {}", this.page.getTargetFile() );
            } catch ( final SocketTimeoutException ex ) {
                // timeout, try again
                add( this.page );
            } catch ( final HttpResponseException ex ) {
                if ( ex.getStatusCode() == 503 ) {
                    // http-statuscode 503
                    logger.info( "HTTP-Status 503 ({}), trying again", pageUrl );
                    add( this.page );
                } else {
                    throw new MDRuntimeException( ex );
                }
            } catch ( final IOException ex ) {
                if ( ex.getMessage().contains( "503" ) ) {
                    // http-statuscode 503
                    logger.info( "HTTP-Status 503 ({}), trying again", pageUrl );
                    add( this.page );
                } else {
                    throw new MDRuntimeException( ex );
                }
            }
            return null;
        }

    }
}
