package de.herrlock.manga.cli;

import static de.herrlock.manga.cli.options.MainOptions.CONSOLE_OPTION;
import static de.herrlock.manga.cli.options.MainOptions.HELP_OPTION;
import static de.herrlock.manga.cli.options.MainOptions.SERVER_OPTION;
import static de.herrlock.manga.cli.options.MainOptions.VERSION_OPTION;
import static de.herrlock.manga.cli.options.MainOptions.VIEWPAGE_OPTION;

import java.util.Arrays;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

import com.google.common.base.Predicate;
import com.google.common.collect.FluentIterable;

import de.herrlock.manga.cli.options.LogOptions;
import de.herrlock.manga.cli.options.MainOptions;
import de.herrlock.manga.cli.options.SubOptions;
import de.herrlock.manga.cli.parser.IgnoreUnknownParser;

/**
 * @author HerrLock
 */
public final class OptionParser {

    public static CommandLineContainer parseOptions( final String... args ) throws ParseException {
        CommandLineContainer cmdContainer = new CommandLineContainer();

        // main-options
        CommandLine mainOptions = getMainOptions( args );
        cmdContainer.setMainCmd( mainOptions );

        // sub-options
        CommandLine subOptions = getSubOptions( mainOptions, args );
        cmdContainer.setSubCmd( subOptions );

        // log-options
        CommandLine logOptions = getLogOptions( args );
        cmdContainer.setLogCmd( logOptions );

        return cmdContainer;
    }

    private static CommandLine getMainOptions( final String... args ) throws ParseException {
        MainOptions mainOptions = new MainOptions();
        Options options = mainOptions.getOptions();
        Properties defaultValues = mainOptions.getDefaultValues();
        return new IgnoreUnknownParser().parse( options, Arrays.copyOf( args, args.length ), defaultValues, false );
    }

    private static CommandLine getSubOptions( final CommandLine mainOptions, final String... args ) throws ParseException {
        String givenMode = FluentIterable.of( CONSOLE_OPTION, VIEWPAGE_OPTION, SERVER_OPTION, HELP_OPTION, VERSION_OPTION )
            .firstMatch( new InCommandline( mainOptions ) ).orNull();
        Options options = SubOptions.getSubOptions( givenMode ).getOptions();
        return new IgnoreUnknownParser().parse( options, Arrays.copyOf( args, args.length ), false );
    }

    private static CommandLine getLogOptions( final String... args ) throws ParseException {
        LogOptions logOptions = new LogOptions();
        Options options = logOptions.getOptions();
        return new IgnoreUnknownParser().parse( options, Arrays.copyOf( args, args.length ), false );
    }

    private OptionParser() {
        // prevent instantiation
    }

    /**
     * @author HerrLock
     */
    public static final class InCommandline implements Predicate<String> {
        private final CommandLine commandline;

        public InCommandline( final CommandLine commandline ) {
            this.commandline = commandline;
        }

        @Override
        public boolean apply( final String input ) {
            return this.commandline.hasOption( input );
        }
    }

    /**
     * @author HerrLock
     */
    public static class CommandLineContainer {

        private CommandLine mainCmd;
        private CommandLine subCmd;
        private CommandLine logCmd;

        CommandLineContainer() {
            // prevent instantiation from outside
        }

        public CommandLine getMainCmd() {
            return this.mainCmd;
        }

        public CommandLine getSubCmd() {
            return this.subCmd;
        }

        public CommandLine getLogCmd() {
            return this.logCmd;
        }

        void setMainCmd( final CommandLine mainCmd ) {
            this.mainCmd = mainCmd;
        }

        void setSubCmd( final CommandLine subCmd ) {
            this.subCmd = subCmd;
        }

        void setLogCmd( final CommandLine logCmd ) {
            this.logCmd = logCmd;
        }
    }

}
