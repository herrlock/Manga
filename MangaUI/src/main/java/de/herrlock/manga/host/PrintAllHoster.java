package de.herrlock.manga.host;

/**
 * @author Jan Rau
 *
 */
public class PrintAllHoster {
    public static void execute() {
        System.out.println( "availabile hoster" );
        for ( Hoster h : Hoster.values() ) {
            System.out.println( h.name() + "\t" + h.getURL() );
        }
    }
}
