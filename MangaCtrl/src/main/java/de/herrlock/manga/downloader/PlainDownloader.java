package de.herrlock.manga.downloader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * Simply starts the download without any confirmation,
 * 
 * @author HerrLock
 */
public final class PlainDownloader extends MDownloader {

    public static void main( String... args ) {
        logger.entry();
        execute();
    }

    public static void execute() {
        logger.entry();
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

    private PlainDownloader( DownloadConfiguration conf ) {
        super( conf );
    }

    @Override
    protected void run() {
        logger.entry();
        try {
            downloadAll();
        } catch ( RuntimeException ex ) {
            JOptionPane.showMessageDialog( null, ex.getStackTrace(), ex.getMessage(), JOptionPane.ERROR_MESSAGE );
            throw ex;
        }
    }

}
