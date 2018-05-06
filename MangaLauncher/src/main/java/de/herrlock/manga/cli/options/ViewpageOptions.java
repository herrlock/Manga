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

    public static final String FOLDER_OPTION = "folder";
    public static final String HTML_OPTION = "html";
    public static final String ARCHIVE_OPTION = "archive";
    public static final String CLEAN_OPTION = "clean";
    public static final String CLEAR_OPTION = "clear";

    private final Option folderOption = Option.builder() //
        .longOpt( FOLDER_OPTION ) //
        .hasArg() //
        .type( File.class ) //
        .desc( "The folder to create the files in" ) //
        .build();
    private final Option htmlOption = Option.builder() //
        .longOpt( HTML_OPTION ) //
        .desc( "Create html-resources" ) //
        .build();
    private final Option archiveOption = Option.builder() //
        .longOpt( ARCHIVE_OPTION ) //
        .hasArg() //
        .argName( "format" ) //
        .desc( "Create a comicbook-archive in the given format [CBZ, CBT]" ) //
        .build();
    private final Option cleanOption = Option.builder( "c" ) //
        .longOpt( CLEAN_OPTION ) //
        .desc( "Whether to remove the downloaded chapters after archiving them" ) //
        .build();
    private final Option clearOption = Option.builder() //
        .longOpt( CLEAR_OPTION ) //
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
