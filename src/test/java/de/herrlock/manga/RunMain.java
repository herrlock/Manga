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
        Main.main( arguments );
    }
}
