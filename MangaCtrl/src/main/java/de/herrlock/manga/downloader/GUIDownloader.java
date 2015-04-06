package de.herrlock.manga.downloader;

import java.util.Properties;

import javax.swing.JOptionPane;

import de.herrlock.manga.ui.main.MDGuiController;

public final class GUIDownloader extends MDownloader {

    public static void execute() {
        Properties p = MDGuiController.getProperties();
        new GUIDownloader( p ).run();
    }

    protected GUIDownloader( Properties p ) {
        super( p );
    }

    @Override
    protected void run() {
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
