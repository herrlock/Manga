package de.herrlock.manga.cli.options;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * A set of {@linkplain Option}s
 * 
 * @author HerrLock
 */
public final class ConsoleOptions extends SubOptions {

    private final Option urlOption = Option.builder( "u" ) //
        .longOpt( "url" ) //
        .hasArg() //
        .argName( "url" ) //
        .desc( "The URL to download from" ) //
        .build();
    private final Option patternOption = Option.builder( "p" ) //
        .longOpt( "pattern" ) //
        .hasArg() //
        .argName( "pattern" ) //
        .desc( "The pattern to use" ) //
        .build();
    private final Option timeoutOption = Option.builder( "t" ) //
        .longOpt( "timeout" ) //
        .hasArg() //
        .argName( "timeout" ) //
        .desc( "The timeout for HTTP-requests in seconds" ) //
        .build();
    private final Option interactiveOption = Option.builder( "i" ) //
        .longOpt( "interactive" ) //
        .desc( "Interactive mode: request confirmation etc. from STDIN" ) //
        .build();

    private final Option showHosterOption = Option.builder() //
        .longOpt( "hoster" ) //
        .desc( "List all availabile Hoster" ) //
        .build();
    private final Option createListOption = Option.builder() //
        .longOpt( "list" ) //
        .desc( "Create a list of all availabile mangas" ) //
        .build();

    private final Options options;

    /**
     * Create new Options
     */
    public ConsoleOptions() {
        this.options = new Options() //
            .addOption( this.urlOption ) //
            .addOption( this.patternOption ) //
            .addOption( this.timeoutOption ) //
            .addOption( this.interactiveOption ) //
            .addOption( this.showHosterOption ) //
            .addOption( this.createListOption ) //
        ;
    }

    /**
     * @return the Options from the class
     */
    @Override
    public Options getOptions() {
        return this.options;
    }

}
