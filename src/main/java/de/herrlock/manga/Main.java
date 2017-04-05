package de.herrlock.manga;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.JarInputStream;
import java.util.jar.Manifest;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.log4j2.util.Log4jConfiguration;
import de.herrlock.manga.cli.CliOptions;
import de.herrlock.manga.cli.MyOptions;
import de.herrlock.manga.downloader.ConsoleDownloader;
import de.herrlock.manga.downloader.DownloadProcessor;
import de.herrlock.manga.exceptions.MDException;
import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.host.PrintAllHoster;
import de.herrlock.manga.html.ViewPageMain;
import de.herrlock.manga.http.ServerMain;
import de.herrlock.manga.index.IndexerMain;
import de.herrlock.manga.util.ClassPathHack;
import de.herrlock.manga.util.Utils;
import de.herrlock.manga.util.configuration.Configuration;
import de.herrlock.manga.util.configuration.DownloadConfiguration;
import de.herrlock.manga.util.configuration.IndexerConfiguration;
import javafx.application.Application;

/**
 * Entrance class for the jar.
 * 
 * @author HerrLock
 */
public final class Main {
    private static final Logger logger = LogManager.getLogger();

    public static final String FX_AVAILABILE = "de.herrlock.manga.fxAvailabile";

    /**
     * Entry point for the application.
     * 
     * @param args
     *            the options passed from the commandline
     */
    public static void execute( final String... args ) {
        try {
            final CommandLine commandline = getCommandlineFromArgs( args );
            logger.debug( Arrays.toString( commandline.getOptions() ) );

            // search for javafx and try to hack it into the system-classloader
            runFxClasspathHack();
            // optional alter loglevel-configuration
            checkLoggerConfiguration( commandline );
            // register MBean
            registerCliMBean( commandline );

            // start running
            handleCommandline( commandline );
        } catch ( ParseException ex ) {
            logger.error( ex.getMessage(), ex );
            printHelp();
        }
    }

    private static void runFxClasspathHack() {
        logger.traceEntry();
        try {
            ClassPathHack.makeSureJfxrtLoaded();
            System.setProperty( FX_AVAILABILE, "true" );
        } catch ( ClassNotFoundException ex ) {
            logger.error( "Could not find jfxrt.jar on the classpath. "
                + "This does not have to be fatal as it may be that JavaFX is not needed" );
        }
    }

    private static CommandLine getCommandlineFromArgs( final String... args ) throws ParseException {
        logger.traceEntry( "args: {}", Arrays.toString( args ) );
        MyOptions myOptions = new MyOptions();
        Options options = myOptions.getOptions();
        Properties defaultValues = myOptions.getDefaultValues();
        return new DefaultParser().parse( options, args, defaultValues );
    }

    private static void checkLoggerConfiguration( final CommandLine commandline ) {
        logger.traceEntry( "Commandline: {}", commandline );
        if ( commandline.hasOption( "log" ) ) {
            String optionValue = commandline.getOptionValue( "log" );
            Log4jConfiguration.changeLevelFilterLevel( optionValue );
        } else if ( commandline.hasOption( "verbose" ) ) {
            Log4jConfiguration.changeLevelFilterLevel( Level.DEBUG );
        } else if ( commandline.hasOption( "quiet" ) ) {
            Log4jConfiguration.changeLevelFilterLevel( Level.WARN );
        }
    }

    private static void registerCliMBean( final CommandLine commandline ) {
        CliOptions cliOptions = new CliOptions( commandline );
        Utils.registerMBean( cliOptions, "de.herrlock.manga:type=commandline" );
    }

    private static void handleCommandline( final CommandLine commandline ) {
        logger.traceEntry( "Commandline: {}", commandline );
        if ( commandline.hasOption( "help" ) ) {
            logger.debug( "Commandline has \"help\", show help" );
            printHelp();
        } else if ( commandline.hasOption( "version" ) ) {
            logger.debug( "Commandline has \"version\", show version" );
            printVersion();
        } else if ( commandline.hasOption( "console" ) ) {
            logger.debug( "Commandline has \"console\", start CLI-Downloader" );
            startCliDownloader( commandline );
        } else if ( commandline.hasOption( "dialog" ) ) {
            logger.debug( "Commandline has \"dialog\", start Dialog-Downloader" );
            startDialogDownloader();
        } else if ( commandline.hasOption( "gui" ) ) {
            logger.debug( "Commandline has \"gui\", launch GUI" );
            startGuiDownloader();
        } else if ( commandline.hasOption( "viewpage" ) ) {
            logger.debug( "Commandline has \"viewpage\", start creating html-resources" );
            startViewpageCreator( commandline );
        } else if ( commandline.hasOption( "server" ) ) {
            logger.debug( "Commandline has \"server\", start Server" );
            startServer( commandline );
        } else {
            logger.debug( "else, don't know what to do" );
        }
    }

    private static void printHelp() {
        logger.traceEntry();

        final HelpFormatter helpFormatter = new HelpFormatter();
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printwriter = new PrintWriter( stringWriter );
        final Options options = new MyOptions().getOptions();

        printwriter.println();

        final String cmdLineSyntax = "java -jar MangaLauncher.jar";
        final String header = "The following commands are supported. "
            + "A '>' indicates that the option defines a mode that is started. " + "Only one of these options should be given.";
        final String footer = "Please consult readme.html for further information.";
        final int width = 100;
        int leftPad = HelpFormatter.DEFAULT_LEFT_PAD;
        int descPad = HelpFormatter.DEFAULT_DESC_PAD;

        helpFormatter.printUsage( printwriter, width, cmdLineSyntax, options );
        printwriter.println();
        helpFormatter.printWrapped( printwriter, width, header );
        printwriter.println();
        helpFormatter.printOptions( printwriter, width, options, leftPad, descPad );
        printwriter.println();
        helpFormatter.printWrapped( printwriter, width, footer );

        logger.info( stringWriter );
    }

    private static void printVersion() {
        logger.traceEntry();

        final Manifest m;
        final URL mangaLauncherJarUrl = Main.class.getProtectionDomain().getCodeSource().getLocation();
        logger.debug( "Reading Manifest from jar at {}", mangaLauncherJarUrl );
        try ( JarInputStream j = new JarInputStream( mangaLauncherJarUrl.openStream() ) ) {
            m = j.getManifest();
        } catch ( IOException ex ) {
            throw new MDRuntimeException( ex );
        }

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printwriter = new PrintWriter( stringWriter );

        printwriter.println();

        Attributes infoAttributes = m.getAttributes( "Info" );
        logger.debug( "infoAttributes: {}", infoAttributes );
        Attributes gitAttributes = m.getAttributes( "Git" );
        logger.debug( "gitAttributes: {}", gitAttributes );
        if ( infoAttributes == null || gitAttributes == null ) {
            printwriter.println( "Cannot read all data, probably a development-version launched from an IDE." );
        } else {
            printwriter.println( "MangaDownloader" );
            printwriter.println( "  Version: " + infoAttributes.getValue( "Version" ) );
            printwriter.println();
            printwriter.println( "Details: " );
            printwriter.println( "  Built at: " + infoAttributes.getValue( "Built-At" ) );
            printwriter.println( "  Commit: " + gitAttributes.getValue( "Commit" ) + ":" + gitAttributes.getValue( "Branch" )
                + " (" + gitAttributes.getValue( "Date" ) + ")" );
        }

        logger.info( stringWriter );
    }

    private static void startDialogDownloader() {
        logger.traceEntry();
        logger.info( "Starting Dialog-Downloader:" );
        logger.error( "not yet implemented" );
    }

    private static void startCliDownloader( final CommandLine commandline ) {
        logger.traceEntry( "Commandline: {}", commandline );

        if ( commandline.hasOption( "hoster" ) ) {
            logger.info( "Printing all Hoster:" );
            PrintAllHoster.printHoster( System.out );
        } else {
            Properties properties = Utils.newPropertiesBuilder() //
                .setProperty( Configuration.URL, commandline.getOptionValue( "url" ) ) //
                .setProperty( Configuration.PROXY, commandline.getOptionValue( "proxy" ) ) //
                .setProperty( Configuration.PATTERN, commandline.getOptionValue( "pattern" ) ) //
                .setProperty( Configuration.TIMEOUT, commandline.getOptionValue( "timeout" ) ) //
                .setProperty( Configuration.HEADLESS, String.valueOf( commandline.hasOption( 'i' ) ) ) //
                .build();
            if ( commandline.hasOption( "list" ) ) {
                logger.info( "Creating index" );
                IndexerConfiguration conf = IndexerConfiguration.create( properties );
                logger.info( conf );
                IndexerMain.writeIndex( conf );
            } else {
                logger.info( "Starting Commandline-Downloader:" );
                DownloadConfiguration conf = DownloadConfiguration.create( properties );
                logger.info( conf );
                ConsoleDownloader downloader = new ConsoleDownloader( conf, conf.isHeadless() );
                DownloadProcessor.getInstance().addDownload( downloader );
            }
        }
    }

    private static void startGuiDownloader() {
        logger.traceEntry();
        logger.info( "Starting GUI:" );
        Application.launch( Ctrl.class );
    }

    private static void startViewpageCreator( final CommandLine commandline ) {
        logger.traceEntry( "Commandline: {}", commandline );
        logger.info( "Starting ViewpageCreator:" );
        if ( commandline.hasOption( "folder" ) ) {
            try {
                File file = ( File ) commandline.getParsedOptionValue( "folder" );
                if ( file.exists() ) {
                    if ( file.isDirectory() ) {
                        ViewPageMain.execute( file );
                    } else {
                        logger.error( "The folder \"{}\" must be a folder", file.getAbsolutePath() );
                    }
                } else {
                    logger.error( "The folder \"{}\" does not exist", file.getAbsolutePath() );
                }
            } catch ( ParseException ex ) {
                logger.error( ex );
            }
        } else {
            ViewPageMain.execute();
        }
    }

    private static void startServer( final CommandLine commandline ) {
        logger.traceEntry( "Commandline: {}", commandline );
        logger.info( "Starting Server:" );
        try {
            if ( commandline.hasOption( "browser" ) ) {
                logger.info( "(and browser)" );
                ServerMain.execute( true );
            } else {
                ServerMain.execute( false );
            }
        } catch ( IOException | MDException | URISyntaxException ex ) {
            throw new MDRuntimeException( ex );
        }
    }

    /**
     * private constructor to avoid instantiation
     */
    private Main() {
        // not used
    }
}
