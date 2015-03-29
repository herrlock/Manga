package de.herrlock.manga.downloader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

import de.herrlock.manga.html.ViewPageMain;
import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.Utils;

public final class DialogDownloader extends MDownloader {

    public static void execute() {
        Properties p = new Properties();
        // load properties
        try ( InputStream fIn = new FileInputStream( Constants.SETTINGS_FILE ) ) {
            p.load( fIn );
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
        // properties loaded successful
        DialogDownloader dd = new DialogDownloader( p );
        dd.start();
        try {
            // wait for downloader to complete
            dd.join();
        } catch ( InterruptedException ex ) {
            throw new RuntimeException( ex );
        }
        // create html pages in case no pattern is set
        String cp = Utils.getPattern();
        if ( cp == null || "".equals( cp ) ) {
            ViewPageMain.execute( dd.getTargetFolder() );
        }
    }

    protected DialogDownloader( Properties p ) {
        super( p );
    }

    @Override
    protected void runX() {
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
