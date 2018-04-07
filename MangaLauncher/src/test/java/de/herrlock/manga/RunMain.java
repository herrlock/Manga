package de.herrlock.manga;

/**
 * A class to start Launcher.main without having to alter the IDE's run-configuration<br/>
 * Please do not commit changes to this file
 * 
 * @author HerrLock
 */
public final class RunMain {
    public static void main( final String... args ) {
        // enter CLI-arguments as string-array here
        String[] arguments = //
            {
                "--help"
            };
        Launcher.main( arguments );
    }

    private RunMain() {
        // not used
    }
}
