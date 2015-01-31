package de.herrlock.manga.downloader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import de.herrlock.manga.util.Utils;

public abstract class MDownloader {

    protected ChapterListContainer clc;
    protected PictureMapContainer pmc;
    protected List<Page> dlQueue = new ArrayList<>( 0 );

    final PrintWriter trace;

    public MDownloader( OutputStream out ) {
        this.trace = new PrintWriter( new OutputStreamWriter( out, StandardCharsets.UTF_8 ) );
    }

    public abstract void run();

    public void initCLC() throws IOException {
        this.trace.println( "initCLC()" );
        this.clc = new ChapterListContainer();
    }

    public void initPMC() throws IOException {
        this.trace.println( "initPMC()" );
        this.pmc = new PictureMapContainer( this.clc );
    }

    public int getCLCSize() {
        return this.clc != null ? this.clc.getSize() : 0;
    }

    public int getPMCSize() {
        return this.pmc != null ? this.pmc.getSize() : 0;
    }

    public void downloadAll() throws IOException {
        this.trace.println( "downloadAll()" );
        if ( this.pmc != null ) {
            Map<String, Map<Integer, URL>> picturemap = this.pmc.getPictureMap();
            if ( picturemap != null ) {
                List<String> keys = new ArrayList<>( picturemap.keySet() );
                Collections.sort( keys );
                for ( String key : keys ) {
                    downloadChapter( key );
                }
            } else {
                throw new RuntimeException( "PageMap not initialized" );
            }
        }
    }

    private void downloadChapter( String key ) throws IOException {
        this.trace.println( "downloadChapter( " + key + " )" );
        if ( this.clc != null && this.pmc != null ) {
            Map<Integer, URL> urlMap = this.pmc.getUrlMap( key );
            System.out.println( "Download chapter " + key + " (" + urlMap.size() + " pages)" );
            File chapterFolder = new File( this.clc.getPath(), key );
            if ( chapterFolder.exists() || chapterFolder.mkdirs() ) {
                // add pictures to queue
                for ( Map.Entry<Integer, URL> e : urlMap.entrySet() ) {
                    this.dlQueue.add( new Page( e.getValue(), chapterFolder, e.getKey() ) );
                }
                // start download
                downloadPages();
            }
            System.out.println( "finished chapter " + key );
        }
    }

    private void downloadPages() throws IOException {
        this.trace.println( "downloadPages()" );
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
        this.trace.println( "dlPic( " + c + " )" );
        if ( this.clc != null ) {
            URL imageUrl = this.clc.getImageLink( c.pageUrl );
            URLConnection con = Utils.getConnection( imageUrl );
            try ( InputStream in = con.getInputStream() ) {
                BufferedImage image = ImageIO.read( in );
                File output = new File( c.chapterFolder, c.pageNumber + ".jpg" );
                ImageIO.write( image, "jpg", output );
            } catch ( SocketException | SocketTimeoutException ex ) {
                this.dlQueue.add( c );
            }
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
