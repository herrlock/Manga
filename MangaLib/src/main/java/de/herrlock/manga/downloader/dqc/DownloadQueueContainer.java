package de.herrlock.manga.downloader.dqc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;

import org.apache.commons.io.FileUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.util.EntityUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.downloader.clc.ChapterListContainer;
import de.herrlock.manga.downloader.pmc.EntryList;
import de.herrlock.manga.ui.log.LogWindow;
import de.herrlock.manga.util.Utils;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

public final class DownloadQueueContainer {
    static final Logger logger = LogManager.getLogger();

    private final List<Page> dlQueue = new ArrayList<>();
    private final ChapterListContainer clc;
    final DownloadConfiguration conf;

    public DownloadQueueContainer( ChapterListContainer clc, DownloadConfiguration conf ) {
        this.clc = clc;
        this.conf = conf;
    }

    /**
     * adds a page to thie queue
     * 
     * @param p
     *            the {@link Page} to add
     */
    public void add( Page p ) {
        this.dlQueue.add( p );
    }

    /**
     * adds a new {@link Page} with the given parameters to the queue
     * 
     * @see Page#Page(URL, File, int)
     */
    public void add( URL pageUrl, File chapterFolder, int pageNumber ) {
        this.add( new Page( pageUrl, chapterFolder, pageNumber ) );
    }

    /**
     * adds a {@link Collection} of Integer-URL-Pairs to this Container
     * 
     * @see DownloadQueueContainer#add(URL, File, int)
     */
    public void addEntryList( File chapterFolder, EntryList<Integer, URL> entryList ) {
        for ( Entry<Integer, URL> e : entryList ) {
            this.add( e.getValue(), chapterFolder, e.getKey().intValue() );
        }
    }

    /**
     * downloads all pages in the queue.<br>
     * clears the queue, calls itself in case a download timed out
     */
    public void downloadPages() {
        logger.entry();
        List<Page> pages = Collections.unmodifiableList( new ArrayList<>( this.dlQueue ) );
        this.dlQueue.clear();
        // download pictures from the ChapterListContainer
        List<DownloadThread> callables = new ArrayList<>( pages.size() );
        for ( Page p : pages ) {
            callables.add( new DownloadThread( p ) );
        }
        LogWindow.setChapterProgress( 0 );
        LogWindow.setChapterProgressMax( callables.size() * 3 );
        Utils.callCallables( callables );
        if ( !this.dlQueue.isEmpty() ) {
            downloadPages();
        }
    }

    URL getImageLink( URL pageUrl ) throws IOException {
        return this.clc.getImageLink( pageUrl );
    }

    /**
     * A Thread to download one image
     * 
     * @author HerrLock
     */
    private final class DownloadThread implements Callable<Void> {
        private final Page p;
        private final ResponseHandler<Void> handler;

        public DownloadThread( final Page p ) {
            logger.entry( p.getUrl() );
            this.p = p;
            this.handler = new ResponseHandler<Void>() {
                @Override
                public Void handleResponse( HttpResponse response ) throws ClientProtocolException, IOException {
                    HttpEntity entity = response.getEntity();
                    try ( InputStream in = entity.getContent() ) {
                        FileUtils.copyInputStreamToFile( in, p.getTargetFile() );
                    } finally {
                        EntityUtils.consume( entity );
                    }
                    return null;
                }
            };
        }

        /**
         * downloads the page
         */
        @Override
        public Void call() {
            try {
                LogWindow.setChapterProgressPlusOne();
                URL imageURL = DownloadQueueContainer.this.getImageLink( this.p.getUrl() );
                File outputFile = this.p.getTargetFile();
                LogWindow.setChapterProgressPlusOne();
                logger.debug( "start reading image " + imageURL );
                Utils.getDataAndExecuteResponseHandler( imageURL, DownloadQueueContainer.this.conf, this.handler );
                logger.debug( "saved image to " + outputFile );
                LogWindow.setChapterProgressPlusOne();
            } catch ( SocketException | SocketTimeoutException ex ) {
                DownloadQueueContainer.this.add( this.p );
            } catch ( IOException ex ) {
                if ( ex.getMessage().contains( "503" ) ) {
                    // http-statuscode 503
                    System.out.println( "HTTP-Status 503 (" + this.p.getUrl() + "), trying again" );
                    DownloadQueueContainer.this.add( this.p );
                } else {
                    throw new RuntimeException( ex );
                }
            }
            return null;
        }
    }

    protected static final class Page {
        /**
         * the {@link URL} where to read this page from
         */
        private final URL pageUrl;
        /**
         * the {@link File} where this page will be saved
         */
        private final File targetFile;

        /**
         * creates a new page
         * 
         * @param pageUrl
         *            the {@link URL} where to read from
         * @param chapterFolder
         *            the folder where the page will be stored
         * @param pageNumber
         *            the number of the page, sets the filename (with optional prepended '0')
         */
        public Page( URL pageUrl, File chapterFolder, int pageNumber ) {
            this.pageUrl = pageUrl;
            String _nr = ( pageNumber > 9 ? "" : "0" ) + pageNumber;
            this.targetFile = new File( chapterFolder, _nr + ".jpg" );
        }

        /**
         * getter for this page's {@link URL}
         * 
         * @return the {@link URL} of this page
         */
        public URL getUrl() {
            return this.pageUrl;
        }

        /**
         * getter for this page's save-location
         * 
         * @return the {@link File} where to store this page
         */
        public File getTargetFile() {
            return this.targetFile;
        }
    }
}
