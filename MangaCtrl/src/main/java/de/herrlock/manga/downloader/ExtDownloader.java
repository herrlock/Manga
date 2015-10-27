package de.herrlock.manga.downloader;

import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.javafx.Dialogs;
import de.herrlock.javafx.Dialogs.Response;
import de.herrlock.manga.util.CtrlUtils;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * A Downloader-Implementation that gets its Configuration from any external source.
 * 
 * @author HerrLock
 */
public final class ExtDownloader extends MDownloader {
    private static final Logger logger = LogManager.getLogger();

    public static void execute( final Properties p ) {
        logger.entry();
        DownloadConfiguration conf = DownloadConfiguration.create( p );
        new ExtDownloader( conf ).run();
    }

    public static void execute( final DownloadConfiguration conf ) {
        logger.entry();
        new ExtDownloader( conf ).run();
    }

    public ExtDownloader( final DownloadConfiguration p ) {
        super( p );
    }

    @Override
    protected void run() {
        logger.entry();
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
        String message = "number of pictures: " + getPMCSize();
        Response chosen = Dialogs.showConfirmDialog( null, message, "go on?" );
        return chosen == Response.YES;
    }
}
