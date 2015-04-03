package de.herrlock.manga.host;

import java.io.PrintStream;

/**
 * @author HerrLock
 */
public final class PrintAllHoster {
    public static void execute() {
        printHoster( System.out );
    }

    public static void printHoster( PrintStream out ) {
        out.println( "availabile hoster" );
        Hoster[] values = Hoster.sortedValues();
        for ( Hoster h : values ) {
            out.println( h );
        }
    }
    private PrintAllHoster() {
        // nothing to do
    }
}
