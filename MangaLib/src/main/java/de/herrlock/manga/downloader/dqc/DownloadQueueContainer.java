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

    public void add( URL pageUrl, File chapterFolder, int pageNumber ) {
        this.add( new Page( pageUrl, chapterFolder, pageNumber ) );
    }

    public void add( Page p ) {
        this.dlQueue.add( p );
    }

    public boolean isEmpty() {
        return this.dlQueue.isEmpty();
    }

    /**
     * creates a new {@linkplain Collections#unmodifiableList(List) unmodifiable List} containing the current elements of this
     * list.<br>
     * {@linkplain List#clear() clear}s this list afterwards
     * 
     * @return a new list with the elements of this list
     */
    public List<Page> getNewList() {
        List<Page> list = Collections.unmodifiableList( new ArrayList<>( this.dlQueue ) );
        this.dlQueue.clear();
        return list;
    }

    public void downloadPages( ChapterListContainer clc ) throws IOException {
        Utils.trace( "downloadPages()" );
        List<Page> list = this.getNewList();
        // download pictures from the ChapterListContainer
        List<DownloadThread> threads = new ArrayList<>( list.size() );
        for ( Page p : list ) {
            threads.add( new DownloadThread( p, clc.getImageLink( p.getURL() ) ) );
        }
        Utils.startAndWaitForThreads( threads );
        // download failed pictures from the current chapter
        if ( !this.isEmpty() ) {
            downloadPages( clc );
        }
    }

    private class DownloadThread extends Thread {
        private Page p;
        private URL imageUrl;

        public DownloadThread( Page p, URL imageUrl ) {
            String msg = "new DownloadThread( " + p.getURL() + ", " + imageUrl.toExternalForm() + " )";
            Utils.trace( msg );
            this.p = p;
            this.imageUrl = imageUrl;
        }

        @Override
        public void run() {
            try {
                URLConnection con = Utils.getConnection( this.imageUrl );
                try ( InputStream in = con.getInputStream() ) {
                    System.out.println( "start reading image " + this.imageUrl );
                    BufferedImage image = ImageIO.read( in );
                    System.out.println( "read image " + this.imageUrl );
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
