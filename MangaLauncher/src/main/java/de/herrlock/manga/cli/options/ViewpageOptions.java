package de.herrlock.manga.cli.options;

import java.io.File;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionGroup;
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
    private final Option htmlOption = Option.builder() //
        .longOpt( "html" ) //
        .desc( "Create html-resources" ) //
        .build();
    private final Option archiveOption = Option.builder() //
        .longOpt( "archive" ) //
        .hasArg() //
        .argName( "format" ) //
        .desc( "Create a comicbook-archive in the given format [CBZ, CBT]" ) //
        .build();
    private final Option cleanOption = Option.builder( "c" ) //
        .longOpt( "clean" ) //
        .desc( "Whether to remove the downloaded chapters after archiving them" ) //
        .build();
    private final Option clearOption = Option.builder() //
        .longOpt( "clear" ) //
        .desc( "Same as \"clean\"" ) //
        .build();

    private final OptionGroup typeGroup = new OptionGroup() //
        .addOption( this.htmlOption ) //
        .addOption( this.archiveOption ) //
    ;

    private final Options options;

    /**
     * Create new Options
     */
    public ViewpageOptions() {
        this.typeGroup.setRequired( true );
        this.options = new Options() //
            .addOptionGroup( this.typeGroup ) //
            .addOption( this.folderOption ) //
            .addOption( this.cleanOption ) //
            .addOption( this.clearOption ) //
        ;
    }

    /**
     * @return the Options from the class
     */
    @Override
    public Options getOptions() {
        return this.options;
    }

}
