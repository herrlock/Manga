package de.herrlock.manga.downloader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * simply downloads everything after starting
 * 
 * @author HerrLock
 */
public final class PlainDownloader extends MDownloader {

    public static void main( String... args ) {
        execute();
    }

    public static void execute() {
        Properties p = new Properties();
        // load properties
        try ( InputStream fIn = new FileInputStream( Constants.SETTINGS_FILE ) ) {
            p.load( fIn );
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
        // properties loaded successful
        DownloadConfiguration conf = DownloadConfiguration.create( p );
        new PlainDownloader( conf ).run();
    }

    protected PlainDownloader( DownloadConfiguration conf ) {
        super( conf );
    }

    @Override
    protected void run() {
        try {
            downloadAll();
        } catch ( RuntimeException ex ) {
            JOptionPane.showMessageDialog( null, ex.getStackTrace(), ex.getMessage(), JOptionPane.ERROR_MESSAGE );
            throw ex;
        }
    }

}
