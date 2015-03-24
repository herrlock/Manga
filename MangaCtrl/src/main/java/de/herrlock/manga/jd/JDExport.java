package de.herrlock.manga.jd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import de.herrlock.manga.downloader.MDownloader;
import de.herrlock.manga.util.Constants;

public class JDExport extends MDownloader {

    public static void main( String[] args ) {
        execute();
    }

    public static void execute() {
        System.out.println( "add to jd" );
        try {
            Properties p = new Properties();
            try ( InputStream fIn = new FileInputStream( Constants.SETTINGS_FILE ) ) {
                p.load( fIn );
            }
            JDExport jde = new JDExport( p );
            jde.start();
            jde.join();
        } catch ( RuntimeException ex ) {
            throw ex;
        } catch ( Exception ex ) {
            throw new RuntimeException( ex );
        }
    }

    public JDExport( Properties p ) {
        super( p );
    }

    @Override
    protected void runX() {
        try {
            File outFolder = new File( "folderwatch" );
            File path = this.clc.getPath();
            Set<Entry<String, Map<Integer, URL>>> pmcEntries = this.pmc.getPictureMap().entrySet();
            for ( Entry<String, Map<Integer, URL>> entry : pmcEntries ) {
                String chapter = entry.getKey();
                Crawljob c = new Crawljob( new File( path, chapter ), chapter );
                Set<Entry<Integer, URL>> entrySet = entry.getValue().entrySet();
                for ( Entry<Integer, URL> e : entrySet ) {
                    String filename = e.getKey().toString();
                    URL pictureUrl = this.clc.getImageLink( e.getValue() );
                    c.addCrawljobEntry( filename, pictureUrl.toExternalForm() );
                }

                File outFile = new File( outFolder, c.getFilename() );
                try ( OutputStream out = new FileOutputStream( outFile ) ) {
                    out.write( c.export().getBytes( StandardCharsets.UTF_8 ) );
                    System.out.println( "print string -> " + outFile );
                } catch ( IOException ex ) {
                    throw new RuntimeException( ex );
                }
            }
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }
}
