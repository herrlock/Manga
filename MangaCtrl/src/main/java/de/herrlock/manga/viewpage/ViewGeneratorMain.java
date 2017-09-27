package de.herrlock.manga.viewpage;

import java.io.File;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author HerrLock
 */
public final class ViewGeneratorMain {
    @SuppressWarnings( "unused" )
    private static final Logger logger = LogManager.getLogger();

    private static File getFolder() {
        return getFolder( JOptionPane.showInputDialog( "Input the folder where the chapters are." ) );
    }

    private static File getFolder( final String filename ) {
        if ( filename == null ) {
            return null;
        }
        File file = new File( filename );
        if ( file.exists() ) {
            return file;
        }
        return null;
    }

    public static void executeViewHtml() {
        File folder = getFolder();
        if ( folder == null ) {
            JOptionPane.showMessageDialog( null, "Folder does not exists." );
        } else {
            executeViewHtml( folder );
        }
    }

    /**
     * @param folder
     *            the mangafolder to create a viewer for (has the format {@code <manganame>_<timestamp>})
     */
    public static void executeViewHtml( final File folder ) {
        ViewPage.execute( folder );
    }

    private ViewGeneratorMain() {
        // nothing to do
    }

}
