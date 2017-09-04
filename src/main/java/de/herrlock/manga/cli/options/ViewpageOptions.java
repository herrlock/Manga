package de.herrlock.manga.cli.options;

import java.io.File;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;

/**
 * A set of {@linkplain Option}s
 * 
 * @author HerrLock
 */
public final class ViewpageOptions extends SubOptions {

    private final Option folderOption = Option.builder() //
        .longOpt( "folder" ) //
        .hasArg() //
        .type( File.class ) //
        .desc( "The folder to create the files in" ) //
        .build();

    private final Options options;

    /**
     * Create new Options
     */
    public ViewpageOptions() {
        this.options = new Options().addOption( this.folderOption );
    }

    /**
     * @return the Options from the class
     */
    @Override
    public Options getOptions() {
        return this.options;
    }

}
