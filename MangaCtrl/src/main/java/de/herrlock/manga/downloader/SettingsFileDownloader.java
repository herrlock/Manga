package de.herrlock.manga.downloader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.CtrlUtils;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * Simply starts the download without any confirmation,
 * 
 * @author HerrLock
 */
public final class SettingsFileDownloader extends MDownloader {
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
        SettingsFileDownloader dlImpl = new SettingsFileDownloader( conf );
        DownloadProcessor.getInstance().addDownload( dlImpl );
    }

    public SettingsFileDownloader( final DownloadConfiguration conf ) {
        super( conf );
    }

    @Override
    protected void run() {
        logger.traceEntry();
        try {
            downloadAll();
        } catch ( Exception ex ) {
            CtrlUtils.showErrorDialog( ex );
            throw ex;
        }
    }

}
