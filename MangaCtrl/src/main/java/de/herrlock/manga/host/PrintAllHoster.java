package de.herrlock.manga.host;

import java.io.PrintStream;
import java.text.MessageFormat;
import java.util.ArrayDeque;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author HerrLock
 */
public final class PrintAllHoster {

    private static final Logger logger = LogManager.getLogger();

    public static void printHoster( final PrintStream out ) {
        Collection<Hoster> values = Hosters.sortedValues();
        out.println( "availabile hoster" );
        for ( Hoster h : values ) {
            out.println( getHosterString( h ) );
        }
    }

    public static void printHoster() {
        Collection<Hoster> values = Hosters.sortedValues();
        logger.info( "availabile hoster" );
        for ( Hoster h : values ) {
            logger.info( getHosterString( h ) );
        }
    }

    public static Collection<String> getHosterDescs() {
        Collection<Hoster> values = Hosters.sortedValues();
        Collection<String> result = new ArrayDeque<>( values.size() );
        for ( Hoster h : values ) {
            result.add( getHosterString( h ) );
        }
        return result;
    }

    private static String getHosterString( final Hoster hoster ) {
        return MessageFormat.format( "{0}:\t{1}", hoster.getName(), hoster.getBaseUrl() );
    }

    private PrintAllHoster() {
        // nothing to do
    }
}
