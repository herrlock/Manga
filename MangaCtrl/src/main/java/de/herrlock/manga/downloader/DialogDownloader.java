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
        try {
            Properties p = new Properties();
            try ( InputStream fIn = new FileInputStream( Constants.SETTINGS_FILE ) ) {
                p.load( fIn );
            }
            DialogDownloader dd = new DialogDownloader( p );
            dd.start();
            dd.join();
            String cp = Utils.getPattern();
            if ( cp == null || "".equals( cp ) ) {
                ViewPageMain.execute( dd.getTargetFolder() );
            }
        } catch ( IOException | InterruptedException ex ) {
            throw new RuntimeException( ex );
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
        return JOptionPane.showConfirmDialog( null, message, title, JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE ) == JOptionPane.OK_OPTION;
    }

}
