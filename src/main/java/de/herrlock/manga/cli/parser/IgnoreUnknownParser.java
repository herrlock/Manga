package de.herrlock.manga.cli.parser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.UnrecognizedOptionException;

/**
 * @author HerrLock
 */
public class IgnoreUnknownParser extends DefaultParser {

    @Override
    public CommandLine parse( final Options options, final String[] arguments, final Properties properties,
        final boolean stopAtNonOption ) throws ParseException {
        List<String> argumentList = new ArrayList<>( Arrays.asList( arguments ) );
        CommandLine parse = null;
        while ( parse == null && !argumentList.isEmpty() ) {
            try {
                parse = super.parse( options, argumentList.toArray( new String[argumentList.size()] ), properties, false );
            } catch ( UnrecognizedOptionException ex ) {
                argumentList.remove( ex.getOption() );
            }
        }
        if ( parse == null ) {
            parse = new CommandLine.Builder().build();
        }
        return parse;
    }

}
