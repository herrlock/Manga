package de.herrlock.manga.downloader.dqc;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.imageio.ImageIO;

import de.herrlock.manga.downloader.clc.ChapterListContainer;
import de.herrlock.manga.util.Utils;

public final class DownloadQueueContainer {

    private final List<Page> dlQueue = new ArrayList<>();
    final ChapterListContainer clc;

    public DownloadQueueContainer( ChapterListContainer clc ) {
        this.clc = clc;
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
     * adds a {@link Set} of Integer-URL-Pairs to this Container
     * 
     * @see DownloadQueueContainer#add(URL, File, int)
     */
    public void addEntrySet( File chapterFolder, Set<Entry<Integer, URL>> entrySet ) {
        for ( Entry<Integer, URL> e : entrySet ) {
            this.add( e.getValue(), chapterFolder, e.getKey().intValue() );
        }
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
                URL imageURL = DownloadQueueContainer.this.clc.getImageLink( this.p.getURL() );
                URLConnection con = Utils.getConnection( imageURL );
                try ( InputStream in = con.getInputStream() ) {
                    System.out.println( "start reading image " + imageURL );
                    BufferedImage image = ImageIO.read( in );
                    System.out.println( "read image " + imageURL );
                    File outputFile = this.p.getTargetFile();
                    ImageIO.write( image, "jpg", outputFile );
                    System.out.println( "saved image to " + outputFile );
                }
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
}
