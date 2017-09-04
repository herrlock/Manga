package de.herrlock.manga.cli.options;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * A set of {@linkplain Option}s
 * 
 * @author HerrLock
 */
public final class ServerOptions extends SubOptions {

    private final Option launchBrowserOption = Option.builder() //
        .longOpt( "browser" ) //
        .desc( "Start the browser after starting the server" ) //
        .build();

    private final Options options;

    /**
     * Create new Options
     */
    public ServerOptions() {
        this.options = new Options().addOption( this.launchBrowserOption );
    }

    /**
     * @return the Options from the class
     */
    @Override
    public Options getOptions() {
        return this.options;
    }

}
