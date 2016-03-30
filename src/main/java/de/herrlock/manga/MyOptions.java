package de.herrlock.manga;

import java.io.File;
import java.util.Properties;

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
        .desc( "mode: start the console-downloader" )//
        .build();
    private final Option dialogTypeOption = Option.builder()//
        .longOpt( "dialog" )//
        .desc( "mode: start the dialog-downloader" )//
        .build();
    private final Option guiTypeOption = Option.builder()//
        .longOpt( "gui" )//
        .desc( "mode: start the gui-downloader" )//
        .build();
    private final Option viewpageTypeOption = Option.builder()//
        .longOpt( "viewpage" )//
        .desc( "mode: create files to view the downloaded manga in the browser" )//
        .build();
    private final Option serverTypeOption = Option.builder()//
        .longOpt( "server" )//
        .desc( "mode: start the server to listen to http-requests" )//
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
        .desc( "The URL to download from (mode: console)" )//
        .build();
    private final Option patternOption = Option.builder( "p" )//
        .longOpt( "pattern" )//
        .hasArg()//
        .desc( "The pattern to use (mode: console)" )//
        .build();
    private final Option proxyOption = Option.builder( "x" )//
        .longOpt( "proxy" )//
        .hasArg()//
        .desc( "The Proxy to use (protocol://url:port) (mode: console)" )//
        .build();
    private final Option interactiveOption = Option.builder( "i" )//
        .longOpt( "interactive" )//
        .desc( "Interactive mode: request confirmation etc. from STDIN (mode: console)" )//
        .build();

    private final Option folderOption = Option.builder()//
        .longOpt( "folder" )//
        .hasArg()//
        .type( File.class )//
        .desc( "The folder to create the files in (mode: viewpage)" )//
        .build();

    private final Option launchBrowserOption = Option.builder()//
        .longOpt( "browser" )//
        .desc( "Start the browser after starting the sevrer (mode: server)" )//
        .build();

    private final Option logLevelOption = Option.builder( "l" )//
        .longOpt( "log" )//
        .hasArg()//
        .argName( "level" )//
        .desc( "Loglevel to use. Allowed values: OFF, FATAL, ERROR, WARN, INFO, DEBUG, TRACE, ALL" )//
        .build();

    private final Options options;

    private final Properties defaultValues = new Properties();

    public MyOptions() {
        this.typeOptionGroup.setRequired( true );
        this.options = new Options()//
            .addOptionGroup( this.typeOptionGroup )//
            .addOption( this.urlOption )//
            .addOption( this.patternOption )//
            .addOption( this.proxyOption )//
            .addOption( this.interactiveOption )//
            .addOption( this.folderOption )//
            .addOption( this.launchBrowserOption )//
            .addOption( this.logLevelOption )//
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
