package de.herrlock.manga.downloader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * Starts the Download after printing informations about the number of Pictures to download to STDOUT and receiving a confirmation
 * the proceed by STDIN. Reads the settings from the central settings-file.
 * 
 * @deprecated Maybe use PlainDownloader instead
 * 
 * @author HerrLock
 */
@Deprecated
public final class ConsoleDownloader extends MDownloader {
    private static final Logger logger = LogManager.getLogger();

    private final Scanner sc;

    public static void main( String... args ) {
        logger.entry();
        execute();
    }

    public static void execute() {
        logger.entry();
        Properties p = new Properties();
        try ( InputStream fIn = new FileInputStream( Constants.SETTINGS_FILE ) ) {
            p.load( fIn );
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
        DownloadConfiguration conf = DownloadConfiguration.create( p );
        new ConsoleDownloader( conf, System.in ).run();
    }

    private ConsoleDownloader( DownloadConfiguration conf, InputStream in ) {
        super( conf );
        this.sc = new Scanner( in, "UTF-8" );
    }

    @Override
    protected void run() {
        logger.entry();
        try {
            if ( goon() ) {
                downloadAll();
            }
        } catch ( RuntimeException ex ) {
            throw ex;
        }
    }

    private boolean goon() {
        int noOfPictures = getPMCSize();
        if ( noOfPictures > 0 ) {
            System.out.println( noOfPictures + " page" + ( noOfPictures > 1 ? "s" : "" ) + " availabile" );
            System.out.println( "go on? y|n" );
            try {
                char c = this.sc.next( ".+" ).charAt( 0 );
                return c == 'y' || c == 'Y';
            } catch ( NoSuchElementException ex ) {
                return false;
            }
        }
        System.out.println( "no pages availabile; exiting" );
        return false;
    }
}
