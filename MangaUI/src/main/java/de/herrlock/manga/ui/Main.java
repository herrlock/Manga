package de.herrlock.manga.ui;

import java.util.Properties;

import javax.swing.JOptionPane;

import de.herrlock.manga.Ctrl;
import de.herrlock.manga.downloader.DialogDownloader;
import de.herrlock.manga.util.Constants;

public class Main {

    public static void main( String[] args ) {
        Properties p = new Properties();
        p.put( Constants.PARAM_URL, JOptionPane.showInputDialog( "URL" ) );
        Ctrl.setArguments( p );
        DialogDownloader.execute();
    }

}
