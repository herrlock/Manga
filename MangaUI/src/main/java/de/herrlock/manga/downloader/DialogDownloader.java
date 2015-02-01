package de.herrlock.manga.downloader;

import java.io.IOException;
import java.util.Properties;

import javax.swing.JOptionPane;

import de.herrlock.manga.util.Constants;

public class DialogDownloader extends MDownloader {

    public static void execute() {
        Properties p = new Properties();
        String url = JOptionPane.showInputDialog( "URL" );
        if ( url != null && !"".equals( url ) ) {
            p.put( Constants.PARAM_URL, url );
            try {
                new DialogDownloader( p ).start();
            } catch ( RuntimeException ex ) {
                throw ex;
            } catch ( Exception ex ) {
                throw new RuntimeException( ex );
            }
        }
    }

    protected DialogDownloader( Properties p ) {
        super( p );
    }

    @Override
    protected void runX() {
        try {
            initCLC();
            if ( goonCLC() ) {
                initPMC();
                if ( goonPMC() ) {
                    downloadAll();
                }
            }
        } catch ( IOException ex ) {
            JOptionPane.showMessageDialog( null, ex.getStackTrace(), ex.getMessage(), JOptionPane.ERROR_MESSAGE );
            throw new RuntimeException( ex );
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
