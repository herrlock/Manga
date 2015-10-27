package de.herrlock.manga.downloader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.exceptions.MyException;
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
    private final PrintStream out;

    public static void main( final String... args ) {
        logger.entry();
        execute();
    }

    public static void execute() {
        logger.entry();
        Properties p = new Properties();
        try ( InputStream fIn = new FileInputStream( Constants.SETTINGS_FILE ) ) {
            p.load( fIn );
        } catch ( IOException ex ) {
            throw new MyException( ex );
        }
        DownloadConfiguration conf = DownloadConfiguration.create( p );
        new ConsoleDownloader( conf ).run();
    }

    public ConsoleDownloader( final DownloadConfiguration conf, final InputStream in, final PrintStream out ) {
        super( conf );
        this.out = out;
        this.sc = new Scanner( in, "UTF-8" );
    }

    public ConsoleDownloader( final DownloadConfiguration conf ) {
        this( conf, System.in, System.out );
    }

    @Override
    protected void run() {
        logger.entry();
        if ( goon() ) {
            downloadAll();
        }
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
