package de.herrlock.manga.downloader;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import javax.swing.JOptionPane;

import de.herrlock.manga.util.Constants;

public class DialogDownloader extends MDownloader {

    public static void execute() {
        try ( OutputStream fOut = new FileOutputStream( Constants.TRACE_FILE ) ) {
            MDownloader md = new DialogDownloader( fOut );
            md.run();
        } catch ( RuntimeException ex ) {
            throw ex;
        } catch ( Exception ex ) {
            throw new RuntimeException( ex );
        }
    }

    public DialogDownloader( OutputStream out ) {
        super( out );
    }

    @Override
    public void run() {
        try {
            initCLC();
            if ( goonCLC() ) {
                initPMC();
                if ( goonPMC() ) {
                    downloadAll();
                }
            }
        } catch ( IOException ex ) {
            ex.printStackTrace();
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
