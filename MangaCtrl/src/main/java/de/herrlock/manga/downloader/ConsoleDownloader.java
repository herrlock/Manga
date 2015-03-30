package de.herrlock.manga.downloader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Scanner;

import de.herrlock.manga.util.Constants;

public final class ConsoleDownloader extends MDownloader {

    private final Scanner sc;

    public static void execute() {
        Properties p = new Properties();
        try ( InputStream fIn = new FileInputStream( Constants.SETTINGS_FILE ) ) {
            p.load( fIn );
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
        new ConsoleDownloader( p, System.in ).run();
    }

    protected ConsoleDownloader( Properties p, InputStream in ) {
        super( p );
        this.sc = new Scanner( in, "UTF-8" );
    }

    @Override
    protected void run() {
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
