package de.herrlock.manga.downloader;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
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
import java.util.Properties;

import javax.imageio.ImageIO;

import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.Utils;

public abstract class MDownloader extends Thread {

    protected final DownloadQueueContainer dqc = new DownloadQueueContainer();
    protected ChapterListContainer clc;
    protected PictureMapContainer pmc;

    final PrintWriter trace;

    public MDownloader( Properties p ) {
        Utils.setArguments( p );
        try {
            this.trace = new PrintWriter( new OutputStreamWriter( new FileOutputStream( Constants.TRACE_FILE ),
                StandardCharsets.UTF_8 ) );
        } catch ( FileNotFoundException ex ) {
            throw new RuntimeException( ex );
        }
    }

    @Override
    public void run() {
        try {
            runX();
        } catch ( RuntimeException ex ) {
            System.out.println( ex.getMessage() );
            throw ex;
        } catch ( Exception ex ) {
            System.out.println( ex.getMessage() );
            throw new RuntimeException( ex );
        }
    }
    protected abstract void runX();

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
                    Map<Integer, URL> urlMap = this.pmc.getUrlMap( key );
                    downloadChapter( key, urlMap );
                }
            } else {
                throw new RuntimeException( "PageMap not initialized" );
            }
        } else {
            this.trace.println( "pmc == null" );
        }
    }

    private void downloadChapter( String key, Map<Integer, URL> urlMap ) throws IOException {
        this.trace.println( "downloadChapter( " + key + " )" );
        if ( this.clc != null ) {
            System.out.println( "Download chapter " + key + " (" + urlMap.size() + " pages)" );
            File chapterFolder = new File( this.clc.getPath(), key );
            if ( chapterFolder.exists() || chapterFolder.mkdirs() ) {
                // add pictures to queue
                for ( Map.Entry<Integer, URL> e : urlMap.entrySet() ) {
                    this.dqc.add( new Page( e.getValue(), chapterFolder, e.getKey() ) );
                }
                // start download
                downloadPages();
            } else {
                throw new RuntimeException( chapterFolder + "does not exists and could not be created" );
            }
            System.out.println( "finished chapter " + key );
        } else {
            System.out.println( "clc == null" );
        }
    }

    private void downloadPages() throws IOException {
        this.trace.println( "downloadPages()" );
        List<Page> list = this.dqc.getList();
        this.dqc.clear();
        // download pictures from the current chapter
        for ( Page c : list ) {
            dlPic( c );
        }
        // download failed pictures from the current chapter
        if ( !this.dqc.isEmpty() ) {
            downloadPages();
        }
    }

    private void dlPic( Page c ) throws IOException {
        this.trace.println( "dlPic( " + c + " )" );
        if ( this.clc != null ) {
            URL imageUrl = this.clc.getImageLink( c.getURL() );
            URLConnection con = Utils.getConnection( imageUrl );
            try ( InputStream in = con.getInputStream() ) {
                BufferedImage image = ImageIO.read( in );
                File output = new File( c.getFolder(), c.getNumber() + ".jpg" );
                ImageIO.write( image, "jpg", output );
                System.out.println( "saved image to " + output );
            } catch ( SocketException | SocketTimeoutException ex ) {
                this.dqc.add( c );
            }
        } else {
            System.out.println( "clc == null" );
        }
    }

}
