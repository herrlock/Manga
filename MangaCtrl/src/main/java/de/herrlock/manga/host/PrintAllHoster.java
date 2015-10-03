package de.herrlock.manga.host;

import java.io.PrintStream;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author HerrLock
 */
public final class PrintAllHoster {

    private static final Logger logger = LogManager.getLogger();

    public static void execute() {
        printHoster();
    }

    public static void printHoster( PrintStream out ) {
        out.println( "availabile hoster" );
        Collection<Hoster> values = Hosters.sortedValues();
        for ( Hoster h : values ) {
            out.println( h );
        }
    }

    public static void printHoster() {
        logger.info( "availabile hoster" );
        Collection<Hoster> values = Hosters.sortedValues();
        for ( Hoster h : values ) {
            logger.info( h );
        }
    }

    private PrintAllHoster() {
        // nothing to do
    }
}
