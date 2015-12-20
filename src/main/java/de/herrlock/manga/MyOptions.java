package de.herrlock.manga;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

public final class MyOptions {

    private static final Option CONSOLE_TYPE_OPTION = Option.builder()//
        .longOpt( "console" )//
        .desc( "start the console-downloader" )//
        .build();
    private static final Option DIALOG_TYPE_OPTION = Option.builder()//
        .longOpt( "dialog" )//
        .desc( "start the dialog-downloader" )//
        .build();
    private static final Option GUI_TYPE_OPTION = Option.builder()//
        .longOpt( "gui" )//
        .desc( "start the gui-downloader" )//
        .build();
    private static final Option VIEWPAGE_TYPE_OPTION = Option.builder()//
        .longOpt( "viewpage" )//
        .desc( "create files to view the downloaded manga in the browser" )//
        .build();
    private static final Option SERVER_TYPE_OPTION = Option.builder()//
        .longOpt( "server" )//
        .desc( "start the server to listen to http-requests" )//
        .build();

    private static final OptionGroup TYPE_OPTION_GROUP = new OptionGroup()//
        .addOption( CONSOLE_TYPE_OPTION )//
        .addOption( DIALOG_TYPE_OPTION )//
        .addOption( GUI_TYPE_OPTION )//
        .addOption( VIEWPAGE_TYPE_OPTION )//
        .addOption( SERVER_TYPE_OPTION );

    private static final Option URL_OPTION = Option.builder( "u" )//
        .longOpt( "url" )//
        .hasArg()//
        .desc( "The URL to download from (mode: console)" )//
        .build();
    private static final Option PATTERN_OPTION = Option.builder( "p" )//
        .longOpt( "pattern" )//
        .hasArg()//
        .desc( "The pattern to use (mode: console)" )//
        .build();
    private static final Option PROXY_OPTION = Option.builder( "x" )//
        .longOpt( "proxy" )//
        .hasArg()//
        .desc( "The Proxy to use (protocol://url:port) (mode: console)" )//
        .build();
    private static final Option INTERACTIVE_OPTION = Option.builder( "i" )//
        .longOpt( "interactive" )//
        .desc( "May ask something from STDIN (mode: console)" )//
        .build();
    private static final Option HELP_OPTION = Option.builder( "h" )//
        .longOpt( "help" )//
        .desc( "Show the help" )//
        .build();

    static final Options OPTIONS = new Options()//
        .addOptionGroup( TYPE_OPTION_GROUP )//
        .addOption( URL_OPTION )//
        .addOption( PATTERN_OPTION )//
        .addOption( PROXY_OPTION )//
        .addOption( INTERACTIVE_OPTION )//
        .addOption( HELP_OPTION )//
        ;

    /**
     * private constructor to avoid instantiation
     */
    private MyOptions() {
        // not used
    }
}
