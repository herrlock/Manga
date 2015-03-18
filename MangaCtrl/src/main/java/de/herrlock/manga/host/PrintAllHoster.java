package de.herrlock.manga.host;

import java.io.PrintStream;
import java.util.Arrays;

/**
 * @author Jan Rau
 */
public final class PrintAllHoster {
    public static void execute() {
        printHoster( System.out );
    }

    public static void printHoster( PrintStream out ) {
        out.println( "availabile hoster" );
        Hoster[] values = Hoster.values();
        Arrays.sort( values, Hoster.NAME_COMPARATOR );
        for ( Hoster h : values ) {
            out.println( h );
        }
    }
    private PrintAllHoster() {
        // nothing to do
    }
}
