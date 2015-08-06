package de.herrlock.manga.downloader;

import java.util.Properties;

import javax.swing.JOptionPane;

import de.herrlock.manga.ui.main.MDGuiController;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * A Downloader-Implementation that gets its Configuration from the GUI.
 * 
 * @author HerrLock
 */
public final class GUIDownloader extends MDownloader {

    public static void main( String[] args ) {
        logger.entry();
        execute();
    }

    public static void execute() {
        logger.entry();
        Properties p = MDGuiController.getProperties();
        DownloadConfiguration conf = DownloadConfiguration.create( p );
        new GUIDownloader( conf ).run();
    }

    private GUIDownloader( DownloadConfiguration p ) {
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
