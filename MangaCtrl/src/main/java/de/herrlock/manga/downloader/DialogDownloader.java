package de.herrlock.manga.downloader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.javafx.Dialogs;
import de.herrlock.javafx.Dialogs.Response;
import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.CtrlUtils;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * Initializes the download and starts it after confirming the number of pictures and an estimated whole size of the images with a
 * dialog created in {@link Dialogs}
 * 
 * @author HerrLock
 */
public final class DialogDownloader extends MDownloader {
    private static final Logger logger = LogManager.getLogger();

    public static void execute() {
        logger.traceEntry();
        Properties p = new Properties();
        // load properties
        try ( InputStream fIn = new FileInputStream( Constants.SETTINGS_FILE ) ) {
            p.load( fIn );
        } catch ( IOException ex ) {
            throw new MDRuntimeException( ex );
        }
        // properties loaded successful
        DownloadConfiguration conf = DownloadConfiguration.create( p );
        MDownloader dlImpl = new DialogDownloader( conf );
        DownloadProcessor.getInstance().addDownload( dlImpl );
    }

    public DialogDownloader( final DownloadConfiguration conf ) {
        super( conf );
    }

    @Override
    protected void run() {
        logger.traceEntry();
        try {
            if ( goon() ) {
                downloadAll();
            }
        } catch ( Exception ex ) {
            CtrlUtils.showErrorDialog( ex );
            throw ex;
        }
    }

    private boolean goon() {
        String title = "go on?";
        int noOfPictures = getPMCSize();
        int estimatedSize = noOfPictures * Constants.AVG_SIZE / 1000;
        String message = "Number of pictures: " + noOfPictures + "\n" + "Estimated entire size: " + estimatedSize + " MB";
        Response response = Dialogs.showConfirmDialog( null, message, title );
        return response == Response.YES;
    }

}
