package de.herrlock.manga.downloader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * Initializes the download and starts it after confirming the number of pictures with a {@link JOptionPane}
 * 
 * @author Herrlock
 */
public final class DialogDownloader extends MDownloader {

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
        new DialogDownloader( conf ).run();
    }

    private DialogDownloader( DownloadConfiguration conf ) {
        super( conf );
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
        String title = "go on?";
        String message = "number of pictures: " + getPMCSize();
        int clicked = JOptionPane.showConfirmDialog( null, message, title, JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE );
        return clicked == JOptionPane.OK_OPTION;
    }

}
