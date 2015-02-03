package de.herrlock.manga;

import javax.swing.JOptionPane;

import de.herrlock.manga.downloader.ConsoleDownloader;
import de.herrlock.manga.downloader.DialogDownloader;
import de.herrlock.manga.downloader.UIDownloader;

public class Ctrl {

    public static void main( String[] args ) {
        String message = "Select variant to execute.";
        String title = "Please select";
        int optionType = JOptionPane.DEFAULT_OPTION;
        int messageType = JOptionPane.QUESTION_MESSAGE;
        Object[] options = {
            "Console", "Dialog", "UI"
        };
        int selection = JOptionPane.showOptionDialog( null, message, title, optionType, messageType, null, options, options[0] );
        if ( selection == 0 ) {
            ConsoleDownloader.execute();
        } else if ( selection == 1 ) {
            DialogDownloader.execute();
        } else if ( selection == 2 ) {
            UIDownloader.execute();
        }
    }
}
