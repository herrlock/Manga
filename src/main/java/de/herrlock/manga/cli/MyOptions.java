package de.herrlock.manga.cli;

import java.io.File;
import java.util.Properties;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

/**
 * A set of {@linkplain Option}s
 * 
 * @author HerrLock
 */
public final class MyOptions {

    private final Option consoleTypeOption = Option.builder() //
        .longOpt( "console" ) //
        .desc( " > mode: start the console-downloader" ) //
        .build();
    private final Option dialogTypeOption = Option.builder() //
        .longOpt( "dialog" ) //
        .desc( " > mode: start the dialog-downloader" ) //
        .build();
    private final Option guiTypeOption = Option.builder() //
        .longOpt( "gui" ) //
        .desc( " > mode: start the gui-downloader" ) //
        .build();
    private final Option viewpageTypeOption = Option.builder() //
        .longOpt( "viewpage" ) //
        .desc( " > mode: create files to view the downloaded manga in the browser" ) //
        .build();
    private final Option serverTypeOption = Option.builder() //
        .longOpt( "server" ) //
        .desc( " > mode: start the server to listen to http-requests" ) //
        .build();
    private final Option helpOption = Option.builder( "h" ) //
        .longOpt( "help" ) //
        .desc( " > Show the help and exit" ) //
        .build();
    private final Option versionOption = Option.builder( "v" ) //
        .longOpt( "version" ) //
        .desc( " > Show the version and exit" ) //
        .build();

    private final OptionGroup typeOptionGroup = new OptionGroup() //
        .addOption( this.consoleTypeOption ) //
        .addOption( this.dialogTypeOption ) //
        .addOption( this.guiTypeOption ) //
        .addOption( this.viewpageTypeOption ) //
        .addOption( this.serverTypeOption ) //
        .addOption( this.helpOption ) //
        .addOption( this.versionOption ) //
    ;

    private final Option urlOption = Option.builder( "u" ) //
        .longOpt( "url" ) //
        .hasArg() //
        .argName( "url" ) //
        .desc( "The URL to download from (mode: console)" ) //
        .build();
    private final Option patternOption = Option.builder( "p" ) //
        .longOpt( "pattern" ) //
        .hasArg() //
        .argName( "pattern" ) //
        .desc( "The pattern to use (mode: console)" ) //
        .build();
    private final Option proxyOption = Option.builder( "x" ) //
        .longOpt( "proxy" ) //
        .hasArg() //
        .argName( "proxy" ) //
        .desc( "The Proxy to use (protocol://[user[:password]@]url:port) (mode: console)" ) //
        .build();
    private final Option timeoutOption = Option.builder( "t" ) //
        .longOpt( "timeout" ) //
        .hasArg() //
        .argName( "timeout" ) //
        .desc( "The timeout for HTTP-requests in seconds (mode: console)" ) //
        .build();
    private final Option interactiveOption = Option.builder( "i" ) //
        .longOpt( "interactive" ) //
        .desc( "Interactive mode: request confirmation etc. from STDIN (mode: console)" ) //
        .build();

    private final Option showHosterOption = Option.builder() //
        .longOpt( "hoster" ) //
        .desc( "List all availabile Hoster (mode: console)" ) //
        .build();
    private final Option createListOption = Option.builder() //
        .longOpt( "list" ) //
        .desc( "Create a list of all availabile mangas (mode: console)" ) //
        .build();

    private final Option folderOption = Option.builder() //
        .longOpt( "folder" ) //
        .hasArg() //
        .type( File.class ) //
        .desc( "The folder to create the files in (mode: viewpage)" ) //
        .build();

    private final Option launchBrowserOption = Option.builder() //
        .longOpt( "browser" ) //
        .desc( "Start the browser after starting the server (mode: server)" ) //
        .build();

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

    private final Properties defaultValues = new Properties();

    /**
     * Create new Options
     */
    public MyOptions() {
        this.typeOptionGroup.setRequired( true );
        this.logOptionGroup.setRequired( false );
        this.options = new Options() //
            .addOptionGroup( this.typeOptionGroup ) //
            .addOption( this.urlOption ) //
            .addOption( this.patternOption ) //
            .addOption( this.proxyOption ) //
            .addOption( this.timeoutOption ) //
            .addOption( this.interactiveOption ) //
            .addOption( this.showHosterOption ) //
            .addOption( this.createListOption ) //
            .addOption( this.folderOption ) //
            .addOption( this.launchBrowserOption ) //
            .addOptionGroup( this.logOptionGroup ) //
        ;
        this.defaultValues.setProperty( "gui", "true" );
    }

    /**
     * @return the Options from the class
     */
    public Options getOptions() {
        return this.options;
    }

    /**
     * @return the Properties containing default-values to add
     */
    public Properties getDefaultValues() {
        return this.defaultValues;
    }

}
