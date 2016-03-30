package de.herrlock.manga;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Arrays;
import java.util.Properties;

import javax.servlet.ServletException;

import org.apache.catalina.LifecycleException;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;

import de.herrlock.manga.downloader.ConsoleDownloader;
import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.html.ViewPageMain;
import de.herrlock.manga.http.ServerMain;
import de.herrlock.manga.http.StartWithDesktop;
import de.herrlock.manga.util.ChapterPattern;
import de.herrlock.manga.util.configuration.DownloadConfiguration;
import javafx.application.Application;

/**
 * @author HerrLock
 */
public final class Main {
    private static final Logger logger = LogManager.getLogger();

    /**
     * @param args
     *            the options passed from the commandline
     */
    public static void main( final String... args ) {
        try {
            final CommandLine commandline = getCommandlineFromArgs( args );
            logger.debug( Arrays.toString( commandline.getOptions() ) );

            try {
                ClassPathHack.makeSureJfxrtLoaded();
            } catch ( ClassNotFoundException ex ) {
                logger.error( "Could not find jfxrt.jar on the classpath."
                    + "This does not have to be fatal as it may be that JavaFX is not needed" );
            }

            // optional alter loglevel-configuration
            checkLoggerConfiguration( commandline );

            // start running
            handleCommandline( commandline );
        } catch ( ParseException ex ) {
            logger.warn( ex.getMessage(), ex );
            printHelp();
        }
    }

    private static CommandLine getCommandlineFromArgs( final String... args ) throws ParseException {
        logger.entry( Arrays.toString( args ) );
        MyOptions myOptions = new MyOptions();
        Options options = myOptions.getOptions();
        Properties defaultValues = myOptions.getDefaultValues();
        return new DefaultParser().parse( options, args, defaultValues );
    }

    private static void checkLoggerConfiguration( final CommandLine commandline ) {
        if ( commandline.hasOption( "log" ) ) {
            String optionValue = commandline.getOptionValue( "log" );
            Level level = Level.toLevel( optionValue, Level.INFO );
            Configurator.setRootLevel( level );
            logger.debug( "set log-level to {}", level );
        }
    }

    private static void handleCommandline( final CommandLine commandline ) {
        if ( commandline.hasOption( "help" ) ) {
            logger.trace( "Commandline has \"help\", show help" );
            printHelp();
        } else if ( commandline.hasOption( "console" ) ) {
            logger.trace( "Commandline has \"console\", start CLI-Downloader" );
            startCliDownloader( commandline );
        } else if ( commandline.hasOption( "dialog" ) ) {
            logger.trace( "Commandline has \"dialog\", start Dialog-Downloader" );
            startDialogDownloader();
        } else if ( commandline.hasOption( "gui" ) ) {
            logger.trace( "Commandline has \"gui\", launch GUI" );
            startGuiDownloader();
        } else if ( commandline.hasOption( "viewpage" ) ) {
            logger.trace( "Commandline has \"viewpage\", start creating html-resources" );
            startViewpageCreator( commandline );
        } else if ( commandline.hasOption( "server" ) ) {
            logger.trace( "Commandline has \"server\", start Server" );
            startServer( commandline );
        } else {
            logger.trace( "else, don't know what to do" );
        }
    }

    private static void printHelp() {
        logger.entry();

        final HelpFormatter helpFormatter = new HelpFormatter();
        final StringWriter stringWriter = new StringWriter();
        final PrintWriter printwriter = new PrintWriter( stringWriter );
        final Options options = new MyOptions().getOptions();

        printwriter.println();

        final String cmdLineSyntax = "java -jar MangaLauncher.jar";
        final String header = "";
        final String footer = "";
        final int width = 100;
        int leftPad = HelpFormatter.DEFAULT_LEFT_PAD;
        int descPad = HelpFormatter.DEFAULT_DESC_PAD;

        helpFormatter.printUsage( printwriter, width, cmdLineSyntax, options );
        helpFormatter.printWrapped( printwriter, width, header );
        helpFormatter.printOptions( printwriter, width, options, leftPad, descPad );
        helpFormatter.printWrapped( printwriter, width, footer );

        logger.info( stringWriter );
    }

    private static void startDialogDownloader() {
        logger.entry();
        logger.error( "not yet implemented" );
    }

    private static void startCliDownloader( final CommandLine commandline ) {
        logger.entry( commandline );

        try {
            URL url = new URL( commandline.getOptionValue( "url" ) );
            HttpHost proxy = commandline.hasOption( "proxy" ) ? new HttpHost( commandline.getOptionValue( "proxy" ) ) : null;
            ChapterPattern pattern = commandline.hasOption( "pattern" )
                ? new ChapterPattern( commandline.getOptionValue( "pattern" ) )
                : null;
            DownloadConfiguration conf = new DownloadConfiguration( true, url, proxy, pattern, 0 );
            logger.info( conf );
            boolean interactive = commandline.hasOption( 'i' );
            ConsoleDownloader downloader = new ConsoleDownloader( conf, interactive );
            downloader.run();
        } catch ( MalformedURLException ex ) {
            throw new MDRuntimeException( ex );
        }
    }

    private static void startGuiDownloader() {
        logger.entry();
        Application.launch( Ctrl.class );
    }

    private static void startViewpageCreator( final CommandLine commandline ) {
        logger.entry( commandline );
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
        logger.entry( commandline );
        try {
            if ( commandline.hasOption( "browser" ) ) {
                StartWithDesktop.main();
            } else {
                ServerMain.main();
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
