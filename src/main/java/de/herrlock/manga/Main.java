package de.herrlock.manga;

import static de.herrlock.manga.MyOptions.OPTIONS;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.downloader.ConsoleDownloader;
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
     * @throws MalformedURLException
     */
    public static void main( String... args ) throws Exception {
        CommandLine cl = new DefaultParser().parse( OPTIONS, args );
        logger.debug( Arrays.toString( cl.getOptions() ) );
        if ( cl.getOptions().length == 0 ) {
            // empty arguments
            logger.trace( "empty args, start GUI" );
            startGuiDownloader( args );

        } else if ( cl.hasOption( 'h' ) ) {
            logger.trace( "Commandline has 'h', show help" );
            printHelp();

        } else if ( cl.hasOption( "console" ) ) {
            logger.trace( "Commandline has \"console\", start CLI-Downloader" );
            startCliDownloader( cl );

        } else if ( cl.hasOption( "dialog" ) ) {
            logger.trace( "Commandline has \"dialog\", start Dialog-Downloader" );
            // startDialogDownloader( cl );

        } else if ( cl.hasOption( "gui" ) ) {
            logger.trace( "Commandline has \"gui\", launch GUI" );
            startGuiDownloader( args );

        } else if ( cl.hasOption( "viewpage" ) ) {
            logger.trace( "Commandline has \"viewpage\", start creating html-resources" );
            // startCliDownloader( cl );

        } else if ( cl.hasOption( "server" ) ) {
            logger.trace( "Commandline has \"server\", start Server" );
            // startCliDownloader( cl );

        } else {
            logger.trace( "else, don't know what to do" );
        }
    }

    private static void printHelp() {
        logger.entry();

        HelpFormatter helpFormatter = new HelpFormatter();
        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter( stringWriter );

        pw.println();

        final String cmdLineSyntax = "java -jar MangaLauncher.jar";
        final String header = "";
        final String footer = "";
        final int width = 100;
        int leftPad = HelpFormatter.DEFAULT_LEFT_PAD;
        int descPad = HelpFormatter.DEFAULT_DESC_PAD;

        helpFormatter.printUsage( pw, width, cmdLineSyntax, OPTIONS );
        helpFormatter.printWrapped( pw, width, header );
        helpFormatter.printOptions( pw, width, OPTIONS, leftPad, descPad );
        helpFormatter.printWrapped( pw, width, footer );

        logger.info( stringWriter );
    }

    private static void startCliDownloader( CommandLine cl ) throws MalformedURLException {
        logger.entry( cl );

        URL url = new URL( cl.getOptionValue( "url" ) );
        HttpHost proxy = cl.hasOption( "proxy" ) ? new HttpHost( cl.getOptionValue( "proxy" ) ) : null;
        ChapterPattern pattern = cl.hasOption( "pattern" ) ? new ChapterPattern( cl.getOptionValue( "pattern" ) ) : null;
        DownloadConfiguration conf = new DownloadConfiguration( true, url, proxy, pattern, 0 );
        logger.info( conf );
        boolean interactive = cl.hasOption( 'i' );
        ConsoleDownloader dl = new ConsoleDownloader( conf, interactive );
        dl.run();
    }

    private static void startGuiDownloader( String... args ) {
        logger.entry();
        Application.launch( Ctrl.class, args );
    }

}
