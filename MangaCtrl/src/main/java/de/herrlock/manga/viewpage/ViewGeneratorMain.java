package de.herrlock.manga.viewpage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import javax.swing.JFileChooser;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author HerrLock
 */
public final class ViewGeneratorMain {
    private static final Logger logger = LogManager.getLogger();

    private static File getFolder() throws IOException {
        // TODO
        JFileChooser fc = new JFileChooser( "." );
        fc.setFileSelectionMode( JFileChooser.DIRECTORIES_ONLY );
        int returnVal = fc.showOpenDialog( null );
        logger.info( "returnVal: {}", returnVal );
        logger.info( "selected: {}", fc.getSelectedFile() );

        if ( returnVal == JFileChooser.APPROVE_OPTION ) {
            return fc.getSelectedFile();
        } else if ( returnVal == JFileChooser.CANCEL_OPTION ) {
            throw new IOException( "Aborted" );
        } else if ( returnVal == JFileChooser.ERROR_OPTION ) {
            throw new IOException( "Error" );
        } else {
            throw new IllegalArgumentException();
        }
    }

    private static File queryFolder() throws IOException {
        File file = getFolder();
        if ( !file.exists() ) {
            throw new FileNotFoundException( "The folder \"" + file.getAbsolutePath() + "\" does not exist" );
        }
        if ( !file.isDirectory() ) {
            throw new IOException( "\"" + file.getAbsolutePath() + "\" must be a folder" );
        }
        return file;
    }

    /**
     * @param folder
     *            the mangafolder to create a viewer for (has the format {@code <manganame>_<timestamp>})
     */
    public static void executeViewHtml( final File folder ) {
        try {
            File folderToUse = folder == null ? queryFolder() : folder;
            ViewPage.execute( folderToUse );
        } catch ( IOException ex ) {
            logger.error( ex );
        }
    }

    /**
     * @param folder
     *            The mangafolder to create a viewer for (has the format {@code <manganame>_<timestamp>})
     * @param format
     *            The format of the archive to create
     * @param clean
     *            Whether to remove the images after archiving them
     */
    public static void executeViewArchive( final File folder, final String format, final boolean clean ) {
        try {
            File folderToUse = folder == null ? queryFolder() : folder;
            ViewArchive.execute( folderToUse, format, clean );
        } catch ( IOException ex ) {
            logger.error( ex );
        }
    }

    private ViewGeneratorMain() {
        // nothing to do
    }

}
