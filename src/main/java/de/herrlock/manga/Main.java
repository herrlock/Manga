package de.herrlock.manga;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.http.HttpHost;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.util.ChapterPattern;
import de.herrlock.manga.util.configuration.DownloadConfiguration;
import javafx.application.Application;

/**
 * @author HerrLock
 */
public final class Main {
    private static final Logger logger = LogManager.getLogger();

    private static final Options OPTIONS = new Options()//
        .addOption( Option.builder( "u" )//
            .longOpt( "url" )//
            .hasArg()//
            .desc( "The URL to download from" )//
            .build() )//
        .addOption( Option.builder( "p" )//
            .longOpt( "pattern" )//
            .hasArg()//
            .desc( "The pattern to use" )//
            .build() )//
        .addOption( Option.builder( "x" )//
            .longOpt( "proxy" )//
            .hasArg()//
            .desc( "The Proxy to use (protocol://url:port)" )//
            .build() )//
        .addOption( Option.builder( "i" )//
            .longOpt( "interactive" )//
            .desc( "May ask something from STDIN" )//
            .build() )//
        .addOption( Option.builder( "h" )//
            .longOpt( "help" )//
            .desc( "Show the help" )//
            .build() )//
            ;

    /**
     * @param args
     * @throws MalformedURLException
     */
    public static void main( String... args ) throws Exception {
        CommandLine cl = new DefaultParser().parse( OPTIONS, args );
        logger.debug( Arrays.toString( cl.getOptions() ) );
        if ( cl.hasOption( 'h' ) ) {
            logger.trace( "Commandline has 'h', show help" );
            printHelp();
        } else if ( cl.hasOption( 'u' ) ) {
            logger.trace( "Commandline has 'u', start CLI-Downloader" );
            startCliDownloader( cl );
        } else {
            logger.trace( "else, start GUI" );
            startGuiDownloader( args );
        }
    }

    private static void printHelp() {
        logger.entry();

        HelpFormatter helpFormatter = new HelpFormatter();
        StringWriter stringWriter = new StringWriter();
        PrintWriter pw = new PrintWriter( stringWriter );

        pw.println();

        final String appCommand = "java -jar MangaLauncher.jar";
        helpFormatter.printHelp( pw, 100, appCommand, null, OPTIONS, 0, 4, null, true );

        logger.info( stringWriter );
    }

    private static void startCliDownloader( CommandLine cl ) throws MalformedURLException {
        logger.entry( cl );

        URL url = new URL( cl.getOptionValue( "url" ) );
        HttpHost proxy = cl.hasOption( "proxy" ) ? new HttpHost( cl.getOptionValue( "proxy" ) ) : null;
        ChapterPattern pattern = cl.hasOption( "pattern" ) ? new ChapterPattern( cl.getOptionValue( "pattern" ) ) : null;
        DownloadConfiguration conf = new DownloadConfiguration( true, url, proxy, pattern, 0 );
        logger.info( conf );
        // ConsoleDownloader dl = new ConsoleDownloader( conf, cl.hasOption( 'i' ) );
        // dl.run();
    }

    private static void startGuiDownloader( String... args ) {
        logger.entry();
        Application.launch( Ctrl.class, args );
    }

}
