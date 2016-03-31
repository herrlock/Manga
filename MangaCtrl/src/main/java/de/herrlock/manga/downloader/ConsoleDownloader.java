package de.herrlock.manga.downloader;

import java.io.InputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * Starts the Download after printing informations about the number of Pictures to download to STDOUT and receiving a confirmation
 * the proceed by STDIN. Reads the settings from the central settings-file.
 * 
 * @author HerrLock
 */
public final class ConsoleDownloader extends MDownloader {
    private static final Logger logger = LogManager.getLogger();

    private final Scanner sc;
    private final PrintStream out;
    private final boolean interactive;

    public ConsoleDownloader( final DownloadConfiguration conf, final boolean interactive, final InputStream in,
        final PrintStream out ) {
        super( conf );
        this.sc = new Scanner( in, "UTF-8" );
        this.out = out;
        this.interactive = interactive;
    }

    public ConsoleDownloader( final DownloadConfiguration conf, final boolean interactive ) {
        this( conf, interactive, System.in, System.out );
    }

    @Override
    public void run() {
        logger.entry();
        if ( !this.interactive || goon() ) {
            logger.trace( "start download" );
            downloadAll();
        }
        logger.exit();
    }

    private boolean goon() {
        int noOfPictures = getPMCSize();
        if ( noOfPictures > 0 ) {
            this.out.println( noOfPictures + " page" + ( noOfPictures > 1 ? "s" : "" ) + " availabile" );
            this.out.println( "go on? y|n" );
            try {
                char c = this.sc.next( ".+" ).charAt( 0 );
                return c == 'y' || c == 'Y';
            } catch ( NoSuchElementException ex ) {
                return false;
            }
        }
        this.out.println( "no pages availabile; exiting" );
        return false;
    }
}
