package de.herrlock.manga.downloader;

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
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Scanner;

import javax.imageio.ImageIO;

import de.herrlock.log.Logger;
import de.herrlock.manga.util.Utils;

public class MDownloader {

    /**
     * a Scanner to {@link System.in}
     */
    private static Scanner sc;
    /**
     * the Logger
     */
    private static Logger log = Utils.getLogger();

    public static void execute( InputStream in ) {
        log.trace();
        MDownloader md = new MDownloader();
        try ( Scanner _sc = new Scanner( in, "UTF-8" ) ) {
            sc = _sc;
            md.run();
        } catch ( RuntimeException ex ) {
            log.error( ex );
            throw ex;
        } catch ( Exception ex ) {
            log.error( ex );
            throw new RuntimeException( ex );
        }
    }

    private ChapterListContainer clc;
    private PictureMapContainer pmc;
    private List<Page> dlQueue = new ArrayList<>( 0 );

    private MDownloader() {
        // nothing to init
    }

    private void run() {
        log.trace();
        try {
            this.clc = new ChapterListContainer();
            if ( goon1() ) {
                this.pmc = new PictureMapContainer( this.clc );
                if ( goon2() ) {
                    downloadAll();
                } else {
                    log.none( "bye" );
                }
            } else {
                log.none( "bye" );
            }
        } catch ( IOException ex ) {
            ex.printStackTrace();
        }
    }

    private boolean goon1() {
        int noOfChapters = this.clc.getSize();
        if ( noOfChapters > 0 ) {
            log.none( noOfChapters + " chapter" + ( noOfChapters > 1 ? "s" : "" ) + " availabile." );
            return goon();
        }
        log.warn( "no chapters availabile, exiting" );
        return false;
    }

    private boolean goon2() {
        int noOfPictures = this.pmc.getSize();
        if ( noOfPictures > 0 ) {
            log.none( noOfPictures + " page" + ( noOfPictures > 1 ? "s" : "" ) + " availabile." );
            return goon();
        }
        log.warn( "no pictures availabile, exiting" );
        return false;
    }

    private static boolean goon() {
        log.none( "go on? y|n" );
        try {
            char c = sc.next( ".+" ).charAt( 0 );
            return c == 'y' || c == 'Y';
        } catch ( NoSuchElementException ex ) {
            return false;
        }
    }

    private void downloadAll() throws IOException {
        log.trace();
        Map<String, Map<Integer, URL>> picturemap = this.pmc.getPictureMap();
        if ( picturemap != null ) {
            List<String> keys = new ArrayList<>( picturemap.keySet() );
            Collections.sort( keys );
            for ( String key : keys ) {
                downloadChapter( key );
            }
        } else {
            String message = "PageMap not initialized";
            log.error( message );
            throw new RuntimeException( message );
        }
    }

    private void downloadChapter( String key ) throws IOException {
        Map<Integer, URL> urlMap = this.pmc.getUrlMap( key );
        log.none( "Download chapter " + key + " - " + urlMap.size() + " pages" );
        File chapterFolder = new File( this.clc.getPath(), key );
        if ( chapterFolder.exists() || chapterFolder.mkdirs() ) {
            // add pictures to queue
            for ( Map.Entry<Integer, URL> e : urlMap.entrySet() ) {
                this.dlQueue.add( new Page( e.getValue(), chapterFolder, e.getKey() ) );
            }
            // start download
            downloadPages();
        }
        log.none( "finished chapter " + key );
    }

    private void downloadPages() throws IOException {
        List<Page> list = new ArrayList<>( this.dlQueue );
        this.dlQueue.clear();
        // download pictures from the current chapter
        for ( Page c : list ) {
            dlPic( c );
        }
        // download failed pictures from the current chapter
        if ( !this.dlQueue.isEmpty() ) {
            downloadPages();
        }
    }

    private void dlPic( Page c ) throws IOException {
        URL imageUrl = this.clc.getImageLink( c.pageUrl );
        URLConnection con = Utils.getConnection( imageUrl );
        try ( InputStream in = con.getInputStream() ) {
            log.debug( "read image " + imageUrl );
            BufferedImage image = ImageIO.read( in );
            File output = new File( c.chapterFolder, c.pageNumber + ".jpg" );
            log.debug( "write to " + output );
            ImageIO.write( image, "jpg", output );
            log.info( "Chapter " + c.chapterFolder.getName() + ", Page " + c.pageNumber + " - finished" );
        } catch ( SocketException | SocketTimeoutException ex ) {
            log.warn( "Chapter " + c.chapterFolder.getName() + ", Page " + c.pageNumber + " - " + ex.getMessage() );
            this.dlQueue.add( c );
        }
    }
}

class Page {
    final URL pageUrl;
    final File chapterFolder;
    final int pageNumber;

    public Page( URL pageUrl, File chapterFolder, int pageNumber ) {
        this.pageUrl = pageUrl;
        this.chapterFolder = chapterFolder;
        this.pageNumber = pageNumber;
    }
}
