package de.herrlock.manga;

import org.apache.commons.cli.ParseException;

import de.herrlock.manga.exceptions.MDRuntimeException;

/**
 * A class to start Main.main without having to alter the IDE's run-configuration<br/>
 * Please do not commit changes to this file
 * 
 * @author HerrLock
 */
public class RunMain {
    public static void main( final String... args ) {
        try {
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
        } catch ( ParseException ex ) {
            throw new MDRuntimeException( ex );
        }
    }

    private RunMain() {
        // not used
    }
}
