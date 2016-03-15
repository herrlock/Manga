package de.herrlock.manga;

/**
 * A class to start Main.main without having to alter the IDE's run-configuration<br/>
 * Please do not commit changes to this file
 * 
 * @author HerrLock
 */
public class RunMain {
    public static void main( final String... args ) throws Exception {
        // enter CLI-arguments as string-array here
        String[] arguments = //
        {
            "--help"
        };
        // {
        // "--console", //
        // "--log", "debug", //
        // "-u", "http://mangapanda.com/naruto", //
        // "-p", "8;13;18-22", //
        // "-x", "kunde.proxy.itelligence.de", //
        // };
        Main.main( arguments );
    }
}
