package de.herrlock.manga;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

/**
 * A set of {@linkplain Option}s extracted from {@link Main}
 * 
 * @author HerrLock
 */
public final class MyOptions {

    private final Option consoleTypeOption = Option.builder()//
        .longOpt( "console" )//
        .desc( "start the console-downloader" )//
        .build();
    private final Option dialogTypeOption = Option.builder()//
        .longOpt( "dialog" )//
        .desc( "start the dialog-downloader" )//
        .build();
    private final Option guiTypeOption = Option.builder()//
        .longOpt( "gui" )//
        .desc( "start the gui-downloader" )//
        .build();
    private final Option viewpageTypeOption = Option.builder()//
        .longOpt( "viewpage" )//
        .desc( "create files to view the downloaded manga in the browser" )//
        .build();
    private final Option serverTypeOption = Option.builder()//
        .longOpt( "server" )//
        .desc( "start the server to listen to http-requests" )//
        .build();
    private final Option helpOption = Option.builder( "h" )//
        .longOpt( "help" )//
        .desc( "Show the help" )//
        .build();

    private final OptionGroup typeOptionGroup = new OptionGroup()//
        .addOption( this.consoleTypeOption )//
        .addOption( this.dialogTypeOption )//
        .addOption( this.guiTypeOption )//
        .addOption( this.viewpageTypeOption )//
        .addOption( this.serverTypeOption )//
        .addOption( this.helpOption )//
    ;

    private final Option urlOption = Option.builder( "u" )//
        .longOpt( "url" )//
        .hasArg()//
        .desc( "The URL to download from (only: console)" )//
        .build();
    private final Option patternOption = Option.builder( "p" )//
        .longOpt( "pattern" )//
        .hasArg()//
        .desc( "The pattern to use (only: console)" )//
        .build();
    private final Option proxyOption = Option.builder( "x" )//
        .longOpt( "proxy" )//
        .hasArg()//
        .desc( "The Proxy to use (protocol://url:port) (only: console)" )//
        .build();
    private final Option interactiveOption = Option.builder( "i" )//
        .longOpt( "interactive" )//
        .desc( "Interactive mode: request confirmation etc. from STDIN (only: console)" )//
        .build();

    private final Option logLevelOption = Option.builder( "l" )//
        .longOpt( "log" )//
        .hasArg()//
        .argName( "level" )//
        .desc( "Loglevel to use. Allowed values: OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL" )//
        .build();

    private final Options options;

    public MyOptions() {
        this.options = new Options()//
            .addOptionGroup( this.typeOptionGroup )//
            .addOption( this.urlOption )//
            .addOption( this.patternOption )//
            .addOption( this.proxyOption )//
            .addOption( this.interactiveOption )//
            .addOption( this.logLevelOption )//
        ;
    }

    /**
     * @return the Options from the class
     */
    public Options getOptions() {
        return this.options;
    }

}
