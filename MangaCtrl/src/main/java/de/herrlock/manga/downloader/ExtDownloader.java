package de.herrlock.manga.downloader;

import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * A Downloader-Implementation that gets its Configuration from any external source.
 * 
 * @author HerrLock
 */
public final class ExtDownloader extends MDownloader {
    private static final Logger logger = LogManager.getLogger();

    public static void execute( Properties p ) {
        logger.entry();
        DownloadConfiguration conf = DownloadConfiguration.create( p );
        new ExtDownloader( conf ).run();
    }

    public static void execute( DownloadConfiguration conf ) {
        logger.entry();
        new ExtDownloader( conf ).run();
    }

    private ExtDownloader( DownloadConfiguration p ) {
        super( p );
    }

    @Override
    protected void run() {
        logger.entry();
        try {
            if ( goon() ) {
                downloadAll();
            }
        } catch ( RuntimeException ex ) {
            JOptionPane.showMessageDialog( null, ex.getStackTrace(), ex.getMessage(), JOptionPane.ERROR_MESSAGE );
            throw ex;
        }
    }

    private boolean goon() {
        String message = "number of pictures: " + getPMCSize();
        int chosen = JOptionPane.showConfirmDialog( null, message, "go on?", JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE );
        return chosen == JOptionPane.YES_OPTION;
    }
}
