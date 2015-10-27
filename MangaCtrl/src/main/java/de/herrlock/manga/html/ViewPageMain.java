package de.herrlock.manga.html;

import java.io.File;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author HerrLock
 */
public final class ViewPageMain {

    private static final Logger logger = LogManager.getLogger();

    public static void main( final String... args ) {
        logger.entry();
        execute();
    }

    public static void execute() {
        String input = JOptionPane.showInputDialog( "Input the folder where the chapters are." );
        if ( input != null ) {
            File folder = new File( input );
            if ( folder.exists() ) {
                execute( folder );
            } else {
                JOptionPane.showMessageDialog( null, "Folder does not exists." );
            }
        }
    }

    /**
     * @param folder
     *            the mangafolder to create a viewer for (has the format {@code <manganame>_<timestamp>})
     */
    public static void execute( final File folder ) {
        ViewPage.execute( folder );
    }

    private ViewPageMain() {
        // nothing to do
    }
}
