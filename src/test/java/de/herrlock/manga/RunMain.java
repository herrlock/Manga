package de.herrlock.manga;

import org.apache.commons.cli.ParseException;

import de.herrlock.manga.exceptions.MDRuntimeException;

/**
 * A class to start Main.main without having to alter the IDE's run-configuration<br/>
 * Please do not commit changes to this file
 * 
 * @author HerrLock
 */
public final class RunMain {
    public static void main( final String... args ) {
        try {
            // enter CLI-arguments as string-array here
            String[] arguments = //
                {
                    "--help"
                };
            Main.main( arguments );
        } catch ( ParseException ex ) {
            throw new MDRuntimeException( ex );
        }
    }

    private RunMain() {
        // not used
    }
}
