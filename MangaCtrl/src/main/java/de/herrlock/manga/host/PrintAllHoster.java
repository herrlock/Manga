package de.herrlock.manga.host;

import java.io.PrintStream;
import java.text.MessageFormat;
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

    public static void printHoster( final PrintStream out ) {
        out.println( "availabile hoster" );
        Collection<Hoster> values = Hosters.sortedValues();
        for ( Hoster h : values ) {
            out.println( getHosterString( h ) );
        }
    }

    public static void printHoster() {
        logger.info( "availabile hoster" );
        Collection<Hoster> values = Hosters.sortedValues();
        for ( Hoster h : values ) {
            logger.info( getHosterString( h ) );
        }
    }

    private static String getHosterString( final Hoster hoster ) {
        return MessageFormat.format( "{0}:\t{1}", hoster.getName(), hoster.getBaseUrl() );
    }

    private PrintAllHoster() {
        // nothing to do
    }
}
