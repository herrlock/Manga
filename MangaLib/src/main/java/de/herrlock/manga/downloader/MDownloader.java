package de.herrlock.manga.downloader;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import de.herrlock.manga.downloader.clc.ChapterListContainer;
import de.herrlock.manga.downloader.dqc.DownloadQueueContainer;
import de.herrlock.manga.downloader.pmc.PictureMapContainer;
import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.Utils;

public abstract class MDownloader extends Thread {

    protected final DownloadQueueContainer dqc;
    protected ChapterListContainer clc;
    protected PictureMapContainer pmc;

    final PrintWriter trace;

    public MDownloader( Properties p ) {
        Utils.setArguments( p );
        try {
            this.trace = new PrintWriter( new OutputStreamWriter( new FileOutputStream( Constants.TRACE_FILE ),
                StandardCharsets.UTF_8 ), true );
            this.dqc = new DownloadQueueContainer( this.trace );
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

    public void initCLC() {
        this.trace.println( "initCLC()" );
        System.out.println( "getting number of chapters" );
        try {
            this.clc = new ChapterListContainer();
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }

    public void initPMC() {
        this.trace.println( "initPMC()" );
        System.out.println( "checking chapters for number of pages" );
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
                    downloadChapter( key, picturemap.get( key ) );
                }
            } else {
                throw new RuntimeException( "PageMap not initialized" );
            }
        } else {
            this.trace.println( "pmc == null" );
            System.out.println( "pmc == null" );
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
                    this.dqc.add( e.getValue(), chapterFolder, e.getKey().intValue() );
                }
                // start download
                this.dqc.downloadPages( this.clc );
            } else {
                throw new RuntimeException( chapterFolder + "does not exists and could not be created" );
            }
            System.out.println( "finished chapter " + key );
        } else {
            this.trace.println( "clc == null" );
            System.out.println( "clc == null" );
        }
    }

}
