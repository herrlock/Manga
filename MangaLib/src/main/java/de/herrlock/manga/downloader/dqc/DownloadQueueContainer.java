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

import javax.imageio.ImageIO;

import de.herrlock.manga.downloader.clc.ChapterListContainer;
import de.herrlock.manga.util.Utils;

public class DownloadQueueContainer {

    private final List<Page> dlQueue = new ArrayList<>();
    final ChapterListContainer clc;

    public DownloadQueueContainer( ChapterListContainer clc ) {
        this.clc = clc;
    }

    public void add( URL pageUrl, File chapterFolder, int pageNumber ) {
        this.add( new Page( pageUrl, chapterFolder, pageNumber ) );
    }

    public void add( Page p ) {
        this.dlQueue.add( p );
    }

    public void downloadPages() throws IOException {
        Utils.trace( "downloadPages()" );
        List<Page> list = Collections.unmodifiableList( new ArrayList<>( this.dlQueue ) );
        this.dlQueue.clear();
        // download pictures from the ChapterListContainer
        List<DownloadThread> threads = new ArrayList<>( list.size() );
        for ( Page p : list ) {
            threads.add( new DownloadThread( p ) );
        }
        Utils.startAndWaitForThreads( threads );
        // download failed pictures from the current chapter
        if ( !this.dlQueue.isEmpty() ) {
            downloadPages();
        }
    }

    private class DownloadThread extends Thread {
        private final Page p;

        public DownloadThread( Page p ) {
            String msg = "new DownloadThread( " + p.getURL() + " )";
            Utils.trace( msg );
            this.p = p;
        }

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
                } catch ( SocketException | SocketTimeoutException ex ) {
                    DownloadQueueContainer.this.add( this.p );
                }
            } catch ( IOException ex ) {
                throw new RuntimeException( ex );
            }
        }
    }
}
