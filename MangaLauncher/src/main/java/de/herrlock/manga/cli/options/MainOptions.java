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

    public static final String CONSOLE_OPTION = "console";
    public static final String DIALOG_OPTION = "dialog";
    public static final String VIEWPAGE_OPTION = "viewpage";
    public static final String SERVER_OPTION = "server";
    public static final String HELP_OPTION = "help";
    public static final String VERSION_OPTION = "version";

    private final Option consoleTypeOption = Option.builder() //
        .longOpt( CONSOLE_OPTION ) //
        .desc( "start the console-downloader" ) //
        .build();
    private final Option dialogTypeOption = Option.builder() //
        .longOpt( DIALOG_OPTION ) //
        .desc( "start the dialog-downloader" ) //
        .build();
    private final Option viewpageTypeOption = Option.builder() //
        .longOpt( VIEWPAGE_OPTION ) //
        .desc( "create files to view the downloaded manga" ) //
        .build();
    private final Option serverTypeOption = Option.builder() //
        .longOpt( SERVER_OPTION ) //
        .desc( "start the server to listen to http-requests" ) //
        .build();
    private final Option helpOption = Option.builder( "h" ) //
        .longOpt( HELP_OPTION ) //
        .hasArg( true ) //
        .optionalArg( true ) //
        .argName( "context" ) //
        .desc( "show the help and exit" ) //
        .build();
    private final Option versionOption = Option.builder() //
        .longOpt( VERSION_OPTION ) //
        .desc( "show the version and exit" ) //
        .build();

    private final OptionGroup typeOptionGroup = new OptionGroup() //
        .addOption( this.consoleTypeOption ) //
        .addOption( this.dialogTypeOption ) //
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
        this.defaultValues.setProperty( "help", "true" );
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
