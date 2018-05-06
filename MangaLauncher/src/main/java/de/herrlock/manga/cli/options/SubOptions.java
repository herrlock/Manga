package de.herrlock.manga.cli.options;

import static de.herrlock.manga.cli.options.MainOptions.CONSOLE_OPTION;
import static de.herrlock.manga.cli.options.MainOptions.HELP_OPTION;
import static de.herrlock.manga.cli.options.MainOptions.SERVER_OPTION;
import static de.herrlock.manga.cli.options.MainOptions.VERSION_OPTION;
import static de.herrlock.manga.cli.options.MainOptions.VIEWPAGE_OPTION;

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
        } else if ( CONSOLE_OPTION.equalsIgnoreCase( name ) ) {
            return new ConsoleOptions();
        } else if ( VIEWPAGE_OPTION.equalsIgnoreCase( name ) ) {
            return new ViewpageOptions();
        } else if ( SERVER_OPTION.equalsIgnoreCase( name ) ) {
            return new ServerOptions();
        } else if ( HELP_OPTION.equalsIgnoreCase( name ) ) {
            return new EmptyOptions();
        } else if ( VERSION_OPTION.equalsIgnoreCase( name ) ) {
            return new EmptyOptions();
        } else {
            return new EmptyOptions();
        }
    }
}
