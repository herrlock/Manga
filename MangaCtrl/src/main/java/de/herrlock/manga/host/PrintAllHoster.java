package de.herrlock.manga.host;

import java.io.PrintStream;
import java.util.Collection;

/**
 * @author HerrLock
 */
public final class PrintAllHoster {
    public static void execute() {
        printHoster( System.out );
    }

    public static void printHoster( PrintStream out ) {
        out.println( "availabile hoster" );
        Collection<Hoster> values = Hosters.sortedValues();
        for ( Hoster h : values ) {
            out.println( h );
        }
    }
    private PrintAllHoster() {
        // nothing to do
    }
}
