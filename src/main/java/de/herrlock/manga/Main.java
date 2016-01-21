package de.herrlock.manga;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

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
import de.herrlock.manga.http.Server;
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
     * @throws ParseException
     * @throws MalformedURLException
     */
    public static void main( final String... args ) throws ParseException, MalformedURLException {
        final Options options = new MyOptions().getOptions();
        CommandLine commandline = new DefaultParser().parse( options, args );
        logger.debug( Arrays.toString( commandline.getOptions() ) );

        // optional alter loglevel-configuration
        if ( commandline.hasOption( "log" ) ) {
            String optionValue = commandline.getOptionValue( "log" );
            Level level = Level.toLevel( optionValue, Level.INFO );
            logger.log( Level.ALL, "setting log-level to {}", level );
            Configurator.setRootLevel( level );
        }

        // start running
        if ( commandline.getOptions().length == 0 ) {
            // empty arguments
            logger.trace( "empty args, start GUI" );
            startGuiDownloader();

        } else if ( commandline.hasOption( "help" ) ) {
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
            startViewpageCreator();

        } else if ( commandline.hasOption( "server" ) ) {
            logger.trace( "Commandline has \"server\", start Server" );
            startServer();

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

    private static void startCliDownloader( final CommandLine commandline ) throws MalformedURLException {
        logger.entry( commandline );

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
    }

    private static void startGuiDownloader() {
        logger.entry();
        Application.launch( Ctrl.class );
    }

    private static void startViewpageCreator() {
        logger.entry();
        logger.error( "not yet implemented" );

    }

    private static void startServer() {
        logger.entry();
        try {
            Server.startDefaultServer();
        } catch ( IOException ex ) {
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
