package de.herrlock.manga.util;

import org.apache.commons.cli.CommandLine;

import de.herrlock.manga.util.management.CliOptionsMXBean;

/**
 * @author HerrLock
 */
public class CliOptions implements CliOptionsMXBean {

    private final String mainOption;

    public CliOptions( final CommandLine commandline ) {
        if ( commandline.hasOption( "help" ) ) {
            this.mainOption = "help";
        } else if ( commandline.hasOption( "version" ) ) {
            this.mainOption = "version";
        } else if ( commandline.hasOption( "console" ) ) {
            this.mainOption = "console";
        } else if ( commandline.hasOption( "dialog" ) ) {
            this.mainOption = "dialog";
        } else if ( commandline.hasOption( "gui" ) ) {
            this.mainOption = "gui";
        } else if ( commandline.hasOption( "viewpage" ) ) {
            this.mainOption = "viewpage";
        } else if ( commandline.hasOption( "server" ) ) {
            this.mainOption = "server";
        } else {
            this.mainOption = "<unknown>";
        }
    }

    @Override
    public String getMainOption() {
        return this.mainOption;
    }

}
