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
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.base.MoreObjects;

import de.herrlock.log4j2.util.Log4jConfiguration;
import de.herrlock.manga.cli.CliOptions;
import de.herrlock.manga.cli.OptionParser;
import de.herrlock.manga.cli.OptionParser.CommandLineContainer;
import de.herrlock.manga.cli.options.LogOptions;
import de.herrlock.manga.cli.options.MainOptions;
import de.herrlock.manga.cli.options.SubOptions;
import de.herrlock.manga.downloader.ConsoleDownloader;
import de.herrlock.manga.downloader.DownloadProcessor;
import de.herrlock.manga.exceptions.MDException;
import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.host.PrintAllHoster;
import de.herrlock.manga.http.ServerMain;
import de.herrlock.manga.index.IndexerMain;
import de.herrlock.manga.util.Utils;
import de.herrlock.manga.util.configuration.Configuration;
import de.herrlock.manga.util.configuration.DownloadConfiguration;
import de.herrlock.manga.util.configuration.IndexerConfiguration;
import de.herrlock.manga.viewpage.ViewGeneratorMain;

/**
 * Entrance class for the jar.
 * 
 * @author HerrLock
 */
public final class Main {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Entry point for the application.
     * 
     * @param args
     *            the options passed from the commandline
     */
    public static void execute( final String... args ) {
        try {
            final CommandLineContainer cmdContainer = OptionParser.parseOptions( args );
            logger.debug( Arrays.toString( cmdContainer.getMainCmd().getOptions() ) );

            // optional alter loglevel-configuration
            checkLoggerConfiguration( cmdContainer.getLogCmd() );
            // register MBean
            registerCliMBean( cmdContainer );

            // start running
            handleCommandline( cmdContainer );
        } catch ( ParseException ex ) {
            logger.error( ex.getMessage(), ex );
            printHelp( null );
        }
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

    private static void registerCliMBean( final CommandLineContainer cmdContainer ) {
        CliOptions cliOptions = new CliOptions( cmdContainer );
        Utils.registerMBean( cliOptions, "de.herrlock.manga:type=commandline" );
    }

    private static void handleCommandline( final CommandLineContainer cmdContainer ) {
        CommandLine commandline = cmdContainer.getMainCmd();
        CommandLine subCommandline = cmdContainer.getSubCmd();
        logger.traceEntry( "Commandline: {}", Arrays.toString( commandline.getOptions() ) );
        logger.traceEntry( "SubCommandline: {}", Arrays.toString( commandline.getOptions() ) );
        if ( commandline.hasOption( "help" ) ) {
            logger.debug( "Commandline has \"help\", show help" );
            printHelp( commandline.getOptionValue( "help" ) );
        } else if ( commandline.hasOption( "version" ) ) {
            logger.debug( "Commandline has \"version\", show version" );
            printVersion();
        } else if ( commandline.hasOption( "console" ) ) {
            logger.debug( "Commandline has \"console\", start CLI-Downloader" );
            startCliDownloader( subCommandline );
        } else if ( commandline.hasOption( "dialog" ) ) {
            logger.debug( "Commandline has \"dialog\", start Dialog-Downloader" );
            startDialogDownloader();
        } else if ( commandline.hasOption( "viewpage" ) ) {
            logger.debug( "Commandline has \"viewpage\", start creating html-resources" );
            startViewpageCreator( subCommandline );
        } else if ( commandline.hasOption( "server" ) ) {
            logger.debug( "Commandline has \"server\", start Server" );
            startServer( subCommandline );
        } else {
            logger.debug( "else, don't know what to do" );
        }
    }

    private static void printHelp( final String helpContext ) {
        logger.traceEntry( "{}", helpContext );

        final HelpFormatter helpFormatter = new HelpFormatter();
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printwriter = new PrintWriter( stringWriter );
        final Options mainOptions = new MainOptions().getOptions();
        final Options subOptions = SubOptions.getSubOptions( helpContext ).getOptions();
        final Options logOptions = new LogOptions().getOptions();

        printwriter.println();

        final int width = 100;
        final int leftPad = HelpFormatter.DEFAULT_LEFT_PAD;
        final int descPad = HelpFormatter.DEFAULT_DESC_PAD;

        // print basic-syntax
        helpFormatter.printUsage( printwriter, width, "java -jar MangaLauncher.jar", mainOptions );
        printwriter.println();
        // print header
        helpFormatter.printWrapped( printwriter, width, "The following commands are supported. " );
        printwriter.println();
        // print main-options
        helpFormatter.printWrapped( printwriter, width, "Main-options:" );
        helpFormatter.printOptions( printwriter, width, mainOptions, leftPad, descPad );
        printwriter.println();
        // print sub-options
        if ( subOptions.getOptions().isEmpty() ) {
            final String helpInfo = "To receive information about a specific command enter --help <command>";
            helpFormatter.printWrapped( printwriter, width, helpInfo );
        } else {
            helpFormatter.printWrapped( printwriter, width, "Options in context \"" + helpContext + "\": " );
            helpFormatter.printOptions( printwriter, width, subOptions, leftPad, descPad );
        }
        printwriter.println();
        // print logging-options
        helpFormatter.printWrapped( printwriter, width, "Logging-options:" );
        helpFormatter.printOptions( printwriter, width, logOptions, leftPad, descPad );
        printwriter.println();
        // print footer
        helpFormatter.printWrapped( printwriter, width, "Please consult readme.html for further information." );

        logger.info( stringWriter );
    }

    private static void printVersion() {
        logger.traceEntry();

        final Manifest manifest;
        final URL mangaLauncherJarUrl = Main.class.getProtectionDomain().getCodeSource().getLocation();
        logger.debug( "Reading Manifest from jar at {}", mangaLauncherJarUrl );
        try ( JarInputStream j = new JarInputStream( mangaLauncherJarUrl.openStream() ) ) {
            Manifest m = j.getManifest();
            manifest = MoreObjects.firstNonNull( m, new Manifest() );
        } catch ( IOException ex ) {
            throw new MDRuntimeException( ex );
        }

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printwriter = new PrintWriter( stringWriter );

        printwriter.println();

        Attributes infoAttributes = manifest.getAttributes( "Info" );
        logger.debug( "infoAttributes: {}", infoAttributes );
        Attributes gitAttributes = manifest.getAttributes( "Git" );
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
                ConsoleDownloader downloader;
                try {
                    downloader = new ConsoleDownloader( conf, conf.isHeadless() );
                } catch ( MDRuntimeException ex) {
                    Utils.stopHttpClient();
                    throw ex;
                }
                DownloadProcessor.getInstance().addDownload( downloader );
            }
        }
    }

    private static void startViewpageCreator( final CommandLine commandline ) {
        logger.traceEntry( "Commandline: {}", commandline );
        logger.info( "Starting ViewpageCreator:" );
        try {
            File file = ( File ) commandline.getParsedOptionValue( "folder" );
            if ( commandline.hasOption( "archive" ) ) {
                String format = commandline.getOptionValue( "archive" );
                boolean clean = commandline.hasOption( "clean" );
                ViewGeneratorMain.executeViewArchive( file, format, clean );
            } else if ( commandline.hasOption( "html" ) ) {
                ViewGeneratorMain.executeViewHtml( file );
            } else {
                throw new IllegalArgumentException( "Should not get here" );
            }
        } catch ( ParseException ex ) {
            logger.error( ex );
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
