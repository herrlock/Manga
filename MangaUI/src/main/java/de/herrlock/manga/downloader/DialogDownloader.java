package de.herrlock.manga.downloader;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

import de.herrlock.manga.util.Constants;

public class DialogDownloader extends MDownloader {

    public static void execute() {
        try {
            Properties p = new Properties();
            try ( InputStream fIn = new FileInputStream( Constants.SETTINGS_FILE ) ) {
                p.load( fIn );
            }
            new DialogDownloader( p ).start();
        } catch ( RuntimeException ex ) {
            throw ex;
        } catch ( Exception ex ) {
            throw new RuntimeException( ex );
        }
    }

    protected DialogDownloader( Properties p ) {
        super( p );
    }

    @Override
    protected void runX() {
        try {
            if ( goonCLC() ) {
                initPMC();
                if ( goonPMC() ) {
                    downloadAll();
                }
            }
        } catch ( RuntimeException ex ) {
            JOptionPane.showMessageDialog( null, ex.getStackTrace(), ex.getMessage(), JOptionPane.ERROR_MESSAGE );
            throw ex;
        }
    }

    private boolean goonPMC() {
        return goon( "number of pictures: " + getPMCSize() );
    }

    private boolean goonCLC() {
        return goon( "number of chapters: " + getCLCSize() );
    }

    private static boolean goon( String msg ) {
        return JOptionPane.showConfirmDialog( null, msg, "go on?", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE ) == JOptionPane.OK_OPTION;
    }

}
