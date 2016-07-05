package de.herrlock.manga;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLClassLoader;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Filter;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.AppenderRef;

import de.herrlock.log4j2.filter.LevelFilter;
import de.herrlock.manga.downloader.ConsoleDownloader;
import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.host.PrintAllHoster;
import de.herrlock.manga.html.ViewPageMain;
import de.herrlock.manga.http.ServerMain;
import de.herrlock.manga.util.ChapterPattern;
import de.herrlock.manga.util.configuration.Configuration;
import de.herrlock.manga.util.configuration.Configuration.ProxyStorage;
import de.herrlock.manga.util.configuration.DownloadConfiguration;
import javafx.application.Application;

/**
 * Entrance class for the jar.
 * 
 * @author HerrLock
 */
public final class Main {
    private static final Logger logger = LogManager.getLogger();

    /**
     * Flag for JavaFX. Assume it is not availabile initially.
     */
    private static boolean fxAvailabile;

    /**
     * Entry point for the application.
     * 
     * @param args
     *            the options passed from the commandline
     */
    public static void main( final String... args ) {
        try {
            final CommandLine commandline = getCommandlineFromArgs( args );
            logger.debug( Arrays.toString( commandline.getOptions() ) );

            // search for javafx and try to hack it into the system-classloader
            runFxClasspathHack();
            // optional alter loglevel-configuration
            checkLoggerConfiguration( commandline );

            // start running
            handleCommandline( commandline );
        } catch ( ParseException ex ) {
            logger.warn( ex.getMessage(), ex );
            printHelp();
        }
    }

    /**
     * Return if JavaFX is availabile. This can be useful to show messages rather than receiving exceptions in some hybrid
     * classes.
     * 
     * @return if JavaFX is availabile.
     */
    public static boolean getFxAvailabile() {
        return fxAvailabile;
    }

    private static void runFxClasspathHack() {
        logger.traceEntry();
        try {
            ClassPathHack.makeSureJfxrtLoaded();
            fxAvailabile = true;
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
            Level level = Level.toLevel( optionValue, Level.INFO );

            LoggerContext context = ( LoggerContext ) LogManager.getContext( false );
            List<AppenderRef> appenderRefs = context.getConfiguration().getRootLogger().getAppenderRefs();
            for ( AppenderRef appenderRef : appenderRefs ) {
                if ( "ConsoleLogger".equals( appenderRef.getRef() ) ) {
                    Filter rootFilter = appenderRef.getFilter();
                    if ( rootFilter != null && rootFilter.getClass() == LevelFilter.class ) {
                        LevelFilter levelFilter = ( LevelFilter ) rootFilter;
                        levelFilter.setLevel( level );
                        context.updateLoggers();
                        logger.debug( "set log-level to {}", level );
                    } else {
                        logger.debug( "cannot find LevelFilter" );
                    }
                } else {
                    logger.debug( "Not ConsoleLogger: {}", appenderRef );
                }
            }
        }
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
            + "A '>' indicates that the option defines a mode that is started." + "Only one of these options should be given.";
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

        URL[] urls = {
            Main.class.getProtectionDomain().getCodeSource().getLocation()
        };
        Manifest m = new Manifest();
        try ( URLClassLoader urlClassLoader = new URLClassLoader( urls ) ) {
            try ( InputStream in = urlClassLoader.getResourceAsStream( "META-INF/MANIFEST.MF" ) ) {
                m.read( in );
            }
        } catch ( IOException ex ) {
            throw new MDRuntimeException( ex );
        }

        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printwriter = new PrintWriter( stringWriter );

        printwriter.println();

        Attributes infoAttributes = m.getAttributes( "Info" );
        Attributes gitAttributes = m.getAttributes( "Git" );
        printwriter.println( "MangaDownloader" );
        printwriter.println( "  Version: " + infoAttributes.getValue( "Version" ) );
        printwriter.println();
        printwriter.println( "Details: " );
        printwriter.println( "  Built at: " + infoAttributes.getValue( "Built-At" ) );
        printwriter.println( "  Commit: " + gitAttributes.getValue( "Commit" ) + ":" + gitAttributes.getValue( "Branch" ) + " ("
            + gitAttributes.getValue( "Date" ) + ")" );

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
            logger.info( "Starting Commandline-Downloader:" );
            try {
                URL url = new URL( commandline.getOptionValue( "url" ) );
                ProxyStorage proxy = commandline.hasOption( "proxy" )
                    ? Configuration.createProxyStorage( commandline.getOptionValue( "proxy" ) )
                    : null;
                ChapterPattern pattern = commandline.hasOption( "pattern" )
                    ? new ChapterPattern( commandline.getOptionValue( "pattern" ) )
                    : null;
                DownloadConfiguration conf = new DownloadConfiguration( true, url, proxy, pattern, 0 );
                logger.info( conf );
                boolean interactive = commandline.hasOption( 'i' );
                ConsoleDownloader downloader = new ConsoleDownloader( conf, interactive );
                downloader.run();
            } catch ( MalformedURLException | UnknownHostException ex ) {
                throw new MDRuntimeException( ex );
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
        } catch ( IOException | LifecycleException | ServletException | URISyntaxException ex ) {
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
