package de.herrlock.manga.cli.options;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * A set of {@linkplain Option}s
 * 
 * @author HerrLock
 */
public final class ConsoleOptions extends SubOptions {

    public static final String URL_OPTION = "url";
    public static final String PATTERN_OPTION = "pattern";
    public static final String PROXY_OPTION = "proxy";
    public static final String TIMEOUT_OPTION = "timeout";
    public static final String INTERACTIVE_OPTION = "interactive";
    public static final String HOSTER_OPTION = "hoster";
    public static final String LIST_OPTION = "list";
    public static final String JSON_OPTION = "json";

    private final Option urlOption = Option.builder( "u" ) //
        .longOpt( URL_OPTION ) //
        .hasArg() //
        .argName( "url" ) //
        .desc( "The URL to download from" ) //
        .build();
    private final Option patternOption = Option.builder( "p" ) //
        .longOpt( PATTERN_OPTION ) //
        .hasArg() //
        .argName( "pattern" ) //
        .desc( "The pattern to use" ) //
        .build();
    private final Option proxyOption = Option.builder( "x" ) //
        .longOpt( PROXY_OPTION ) //
        .hasArg() //
        .argName( "proxy" ) //
        .desc( "The Proxy to use (protocol://[user[:password]@]url:port)" ) //
        .build();
    private final Option timeoutOption = Option.builder( "t" ) //
        .longOpt( TIMEOUT_OPTION ) //
        .hasArg() //
        .argName( "timeout" ) //
        .desc( "The timeout for HTTP-requests in seconds" ) //
        .build();
    private final Option interactiveOption = Option.builder( "i" ) //
        .longOpt( INTERACTIVE_OPTION ) //
        .desc( "Interactive mode: request confirmation etc. from STDIN" ) //
        .build();

    private final Option showHosterOption = Option.builder() //
        .longOpt( HOSTER_OPTION ) //
        .desc( "List all availabile Hoster" ) //
        .build();
    private final Option createListOption = Option.builder() //
        .longOpt( LIST_OPTION ) //
        .desc( "Create a list of all availabile mangas" ) //
        .build();
    private final Option fromJsonOption = Option.builder() //
        .longOpt( JSON_OPTION ) //
        .hasArg() //
        .optionalArg( true ) //
        .argName( "json-file" ) //
        .desc( "Start downloads from Json-File" ) //
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
            .addOption( this.fromJsonOption ) //
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
