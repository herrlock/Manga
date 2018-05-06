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

    public static final String LOG_OPTION = "log";
    public static final String QUIET_OPTION = "quiet";
    public static final String VERBOSE_OPTION = "verbose";

    private final Option logLevelOption = Option.builder( "l" ) //
        .longOpt( LOG_OPTION ) //
        .hasArg() //
        .argName( "level" ) //
        .desc( "Loglevel to use. Allowed values: OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL" ) //
        .build();
    private final Option quietOption = Option.builder( "q" ) //
        .longOpt( QUIET_OPTION ) //
        .desc( "Set the log-level to WARN" ) //
        .build();
    private final Option verboseOption = Option.builder( "v" ) //
        .longOpt( VERBOSE_OPTION ) //
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
