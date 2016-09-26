package de.herrlock.manga.cli;

import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Queue;
import java.util.Set;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import com.google.common.collect.ImmutableSet;

import de.herrlock.manga.util.management.CliOptionsMXBean;

/**
 * @author HerrLock
 */
public class CliOptions implements CliOptionsMXBean {

    private final String mainOption;
    private final String[] subOptions;

    private final Set<String> mainOptions = ImmutableSet.of( "help", "version", "console", "dialog", "gui", "viewpage",
        "server" );

    public CliOptions( final CommandLine commandline ) {
        this.mainOption = findMainOption( commandline );
        this.subOptions = findSubOptions( commandline );
    }

    private String findMainOption( final CommandLine commandline ) {
        String mainOption = "";
        for ( String string : this.mainOptions ) {
            if ( commandline.hasOption( string ) ) {
                mainOption = string;
                break;
            }
        }
        return mainOption;
    }

    private String[] findSubOptions( final CommandLine commandline ) {
        Option[] options = commandline.getOptions();
        Queue<String> optionQueue = new ArrayDeque<>( options.length );
        for ( Option option : options ) {
            if ( !this.mainOptions.contains( option.getLongOpt() ) ) {
                String optionWithValue = option.getLongOpt() + ( option.hasArg() ? " " + option.getValue() : "" );
                optionQueue.add( optionWithValue );
            }
        }
        return optionQueue.toArray( new String[optionQueue.size()] );
    }

    @Override
    public String getMainOption() {
        return this.mainOption;
    }

    @Override
    public String[] getSubOptions() {
        return Arrays.copyOf( this.subOptions, this.subOptions.length );
    }

}
