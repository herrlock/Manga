package de.herrlock.manga.downloader.dqc;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;

import de.herrlock.manga.downloader.clc.ChapterListContainer;
import de.herrlock.manga.util.Utils;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

public final class DownloadQueueContainer {

    private final List<Page> dlQueue = new ArrayList<>();
    private final ChapterListContainer clc;
    private final DownloadConfiguration conf;

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
    public void addEntrySet( File chapterFolder, Collection<Entry<Integer, URL>> entrySet ) {
        for ( Entry<Integer, URL> e : entrySet ) {
            this.add( e.getValue(), chapterFolder, e.getKey().intValue() );
        }
    }
    /**
     * adds a {@link Map} of Integer-URL-Pairs to this Container
     * 
     * @see DownloadQueueContainer#add(File, Collection)
     */
    public void addMap( File chapterFolder, Map<Integer, URL> map ) {
        addEntrySet( chapterFolder, map.entrySet() );
    }

    /**
     * downloads all pages in the queue.<br>
     * clears the queue, calls itself in case a download timed out
     */
    public void downloadPages() {
        Utils.trace( "downloadPages()" );
        List<Page> pages = Collections.unmodifiableList( new ArrayList<>( this.dlQueue ) );
        this.dlQueue.clear();
        // download pictures from the ChapterListContainer
        List<DownloadThread> threads = new ArrayList<>( pages.size() );
        for ( Page p : pages ) {
            threads.add( new DownloadThread( p ) );
        }
        Utils.startAndWaitForThreads( threads );
        // download failed pictures from the current chapter
        if ( !this.dlQueue.isEmpty() ) {
            downloadPages();
        }
    }

    URL getImageLink( URL pageUrl ) throws IOException {
        return this.clc.getImageLink( pageUrl );
    }

    URLConnection getConnection( URL url ) throws IOException {
        return Utils.getConnection( url, this.conf );
    }

    /**
     * A Thread to download one image
     * 
     * @author HerrLock
     */
    private final class DownloadThread extends Thread {
        private final Page p;

        public DownloadThread( Page p ) {
            Utils.trace( "new DownloadThread( " + p.getURL() + " )" );
            this.p = p;
        }

        /**
         * downloads the page
         */
        @Override
        public void run() {
            try {
                URL imageURL = DownloadQueueContainer.this.getImageLink( this.p.getURL() );
                URLConnection con = getConnection( imageURL );
                InputStream in = con.getInputStream();
                File outputFile = this.p.getTargetFile();
                System.out.println( "start reading image " + imageURL );
                FileUtils.copyInputStreamToFile( in, outputFile );
                System.out.println( "saved image to " + outputFile );
            } catch ( SocketException | SocketTimeoutException ex ) {
                DownloadQueueContainer.this.add( this.p );
            } catch ( IOException ex ) {
                if ( ex.getMessage().contains( "503" ) ) {
                    // http-statuscode 503
                    System.out.println( "HTTP-Status 503 (" + this.p.getURL() + "), trying again" );
                    DownloadQueueContainer.this.add( this.p );
                } else {
                    throw new RuntimeException( ex );
                }
            }
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
        public URL getURL() {
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
