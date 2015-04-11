package de.herrlock.manga.jd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import de.herrlock.exceptions.InitializeException;
import de.herrlock.manga.downloader.MDownloader;
import de.herrlock.manga.ui.main.MDGuiController;
import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.Utils;

public class JDExport extends MDownloader {

    final File jdfwFolder;
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
        String jdhome = p.getProperty( Constants.PARAM_JDFW );
        if ( jdhome == null || jdhome.trim().isEmpty() ) {
            throw new InitializeException( "\"" + Constants.PARAM_JDFW + "\" must be set" );
        }
        new JDExport( p ).run();
    }

    public JDExport( Properties p ) {
        super( p );
        System.err.println( "initialized MDownloader" );
        this.jdfwFolder = new File( p.getProperty( Constants.PARAM_JDFW ) );
        if ( !( this.jdfwFolder.exists() || this.jdfwFolder.mkdir() ) ) {
            throw new RuntimeException( this.jdfwFolder + " does not exist and could not be created" );
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
                new CrawljobFile( entry.getKey(), entry.getValue().entrySet() ).write();
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
            File outFile = new File( JDExport.this.jdfwFolder, this.c.getFilename() );
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
                String filename = this.e.getKey().toString();
                URL pictureUrl = getImageLink();
                CrawljobFile.this.c.addCrawljobEntry( filename, pictureUrl.toExternalForm() );
            }

            private URL getImageLink() {
                try {
                    return JDExport.this.getImageLink( this.e.getValue() );
                } catch ( SocketTimeoutException ex ) {
                    return getImageLink();
                } catch ( IOException ex ) {
                    throw new RuntimeException( ex );
                }
            }
        }
    }
}
