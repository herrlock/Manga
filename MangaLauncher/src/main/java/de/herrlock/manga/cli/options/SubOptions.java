package de.herrlock.manga.cli.options;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * A set of {@linkplain Option}s
 * 
 * @author HerrLock
 */
public abstract class SubOptions {

    /**
     * @return the Options from the class
     */
    public abstract Options getOptions();

    public static SubOptions getSubOptions( final String name ) {
        if ( name == null ) {
            return new EmptyOptions();
        } else if ( "console".equalsIgnoreCase( name ) ) {
            return new ConsoleOptions();
        } else if ( "viewpage".equalsIgnoreCase( name ) ) {
            return new ViewpageOptions();
        } else if ( "server".equalsIgnoreCase( name ) ) {
            return new ServerOptions();
        } else if ( "help".equalsIgnoreCase( name ) ) {
            return new EmptyOptions();
        } else if ( "version".equalsIgnoreCase( name ) ) {
            return new EmptyOptions();
        } else {
            return new EmptyOptions();
        }
    }
}
