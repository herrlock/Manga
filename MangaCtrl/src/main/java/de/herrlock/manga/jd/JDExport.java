package de.herrlock.manga.jd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import de.herrlock.manga.downloader.MDownloader;
import de.herrlock.manga.ui.main.MDGuiController;
import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.Utils;

public class JDExport extends MDownloader {

    final File jdhome;
    final File path = this.clc.getPath();

    public static void executeGetFileProperties() {
        Properties p = new Properties();
        try {
            try ( InputStream fIn = new FileInputStream( Constants.SETTINGS_FILE ) ) {
                p.load( fIn );
            }
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
        execute( p );
    }

    public static void executeGetGuiProperties() {
        Properties p = MDGuiController.getProperties();
        execute( p );
    }

    private static void execute( Properties p ) {
        System.out.println( "add to jd" );
        String jdhome = p.getProperty( Constants.PARAM_JDHOME );
        if ( jdhome == null || jdhome.trim().isEmpty() ) {
            throw new RuntimeException( "\"" + Constants.PARAM_JDHOME + "\" must be set" );
        }
        new JDExport( p ).run();
    }

    public JDExport( Properties p ) {
        super( p );
        this.jdhome = new File( p.getProperty( Constants.PARAM_JDHOME ) );
        if ( !( this.jdhome.exists() || this.jdhome.mkdir() ) ) {
            throw new RuntimeException( this.jdhome + " does not exist and could not be created" );
        }
    }

    public URL getImageLink( URL pageUrl ) throws IOException {
        return this.clc.getImageLink( pageUrl );
    }

    @Override
    protected void run() {
        try {
            Set<Entry<String, Map<Integer, URL>>> pmcEntries = this.pmc.getPictureMap().entrySet();
            for ( Entry<String, Map<Integer, URL>> entry : pmcEntries ) {
                CrawljobFile cf = new CrawljobFile( entry.getKey(), entry.getValue().entrySet() );
                cf.write();
            }
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }

    private class CrawljobFile {
        final Crawljob c;

        public CrawljobFile( String chapter, Set<Entry<Integer, URL>> entrySet ) {
            this.c = new Crawljob( new File( JDExport.this.path, chapter ), chapter );
            List<Thread> threads = new ArrayList<>( entrySet.size() );
            for ( Entry<Integer, URL> e : entrySet ) {
                threads.add( new CrawljobFileEntry( e ) );
            }
            Utils.startAndWaitForThreads( threads );
        }

        public void write() throws IOException {
            File outFile = new File( new File( JDExport.this.jdhome, "folderwatch" ), this.c.getFilename() );
            try ( OutputStream out = new FileOutputStream( outFile ) ) {
                out.write( this.c.export().getBytes( StandardCharsets.UTF_8 ) );
                System.out.println( "print string -> " + outFile );
            }
        }

        private class CrawljobFileEntry extends Thread {
            private final Entry<Integer, URL> e;

            public CrawljobFileEntry( Entry<Integer, URL> e ) {
                this.e = e;
            }

            @Override
            public void run() {
                try {
                    String filename = this.e.getKey().toString();
                    URL pictureUrl = JDExport.this.getImageLink( this.e.getValue() );
                    CrawljobFile.this.c.addCrawljobEntry( filename, pictureUrl.toExternalForm() );
                } catch ( IOException ex ) {
                    throw new RuntimeException( ex );
                }
            }
        }
    }
}
