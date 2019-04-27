package de.herrlock.manga.cli;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableSet;

import de.herrlock.manga.cli.OptionParser.CommandLineContainer;
import de.herrlock.manga.util.management.CliOptionsMXBean;

/**
 * @author HerrLock
 */
public class CliOptions implements CliOptionsMXBean {

    private final String mainOption;
    private final String[] subOptions;
    private final String[] logOptions;

    private final Set<String> mainOptions = ImmutableSet.of( "help", "version", "console", "dialog", "gui", "viewpage",
        "server" );

    public CliOptions( final CommandLineContainer cmdContainer ) {
        this.mainOption = findMainOption( cmdContainer.getMainCmd() );

        this.subOptions = findSubOptions( cmdContainer.getSubCmd() );
        this.logOptions = findSubOptions( cmdContainer.getLogCmd() );
    }

    private String findMainOption( final CommandLine commandline ) {
        return FluentIterable.from( this.mainOptions ).firstMatch( new OptionParser.InCommandline( commandline ) ).orNull();
    }

    private String[] findSubOptions( final CommandLine commandline ) {
        Option[] options = commandline.getOptions();
        Queue<String> optionQueue = new ArrayDeque<>( options.length );
        for ( Option option : options ) {
            if ( !this.mainOptions.contains( option.getLongOpt() ) ) {
                String optionWithValue = option.getLongOpt() + ( option.hasArg() ? "=" + option.getValue() : "" );
                optionQueue.add( optionWithValue );
            }
        }
        return optionQueue.toArray( new String[0] );
    }

    @Override
    public String getMainOption() {
        return this.mainOption;
    }

    @Override
    public String[] getSubOptions() {
        return Arrays.copyOf( this.subOptions, this.subOptions.length );
    }

    @Override
    public String[] getLogOptions() {
        return Arrays.copyOf( this.logOptions, this.logOptions.length );
    }

}
