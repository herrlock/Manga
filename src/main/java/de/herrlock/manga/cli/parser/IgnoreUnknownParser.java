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
            /* parse == null and argumentList not empty */
            try {
                String[] argumentListArray = argumentList.toArray( new String[argumentList.size()] );
                parse = super.parse( options, argumentListArray, properties, false );
            } catch ( UnrecognizedOptionException ex ) {
                argumentList.remove( ex.getOption() );
            }
        }
        /* parse != null or argumentList is empty */
        if ( parse == null ) {
            /* argumentList is empty */
            parse = super.parse( options, new String[0], properties, false );
        }
        return parse;
    }

}
