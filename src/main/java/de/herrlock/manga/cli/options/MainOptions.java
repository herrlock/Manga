package de.herrlock.manga.cli.options;

import java.util.Properties;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
import org.apache.commons.cli.Options;

/**
 * A set of {@linkplain Option}s
 * 
 * @author HerrLock
 */
public final class MainOptions {

    private final Option consoleTypeOption = Option.builder() //
        .longOpt( "console" ) //
        .desc( "start the console-downloader" ) //
        .build();
    private final Option dialogTypeOption = Option.builder() //
        .longOpt( "dialog" ) //
        .desc( "start the dialog-downloader" ) //
        .build();
    private final Option guiTypeOption = Option.builder() //
        .longOpt( "gui" ) //
        .desc( "start the gui-downloader" ) //
        .build();
    private final Option viewpageTypeOption = Option.builder() //
        .longOpt( "viewpage" ) //
        .desc( "create files to view the downloaded manga" ) //
        .build();
    private final Option serverTypeOption = Option.builder() //
        .longOpt( "server" ) //
        .desc( "start the server to listen to http-requests" ) //
        .build();
    private final Option helpOption = Option.builder( "h" ) //
        .longOpt( "help" ) //
        .hasArg( true ) //
        .optionalArg( true ) //
        .argName( "context" ) //
        .desc( "show the help and exit" ) //
        .build();
    private final Option versionOption = Option.builder() //
        .longOpt( "version" ) //
        .desc( "show the version and exit" ) //
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

    private final Options options;

    private final Properties defaultValues = new Properties();

    /**
     * Create new Options
     */
    public MainOptions() {
        this.typeOptionGroup.setRequired( true );
        this.options = new Options().addOptionGroup( this.typeOptionGroup );
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
