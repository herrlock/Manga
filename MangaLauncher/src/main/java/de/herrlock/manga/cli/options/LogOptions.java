package de.herrlock.manga.cli.options;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

/**
 * A set of {@linkplain Option}s
 * 
 * @author HerrLock
 */
public final class LogOptions {

    private final Option logLevelOption = Option.builder( "l" ) //
        .longOpt( "log" ) //
        .hasArg() //
        .argName( "level" ) //
        .desc( "Loglevel to use. Allowed values: OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL" ) //
        .build();
    private final Option quietOption = Option.builder( "q" ) //
        .longOpt( "quiet" ) //
        .desc( "Set the log-level to WARN" ) //
        .build();
    private final Option verboseOption = Option.builder( "v" ) //
        .longOpt( "verbose" ) //
        .desc( "Set the log-level to DEBUG" ) //
        .build();

    private final OptionGroup logOptionGroup = new OptionGroup() //
        .addOption( this.logLevelOption ) //
        .addOption( this.quietOption ) //
        .addOption( this.verboseOption ) //
    ;

    private final Options options;

    /**
     * Create new Options
     */
    public LogOptions() {
        this.options = new Options().addOptionGroup( this.logOptionGroup );
    }

    /**
     * @return the Options from the class
     */
    public Options getOptions() {
        return this.options;
    }

}
