package de.herrlock.manga.cli.parser;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.junit.Assert;
import org.junit.Test;

/**
 * @author HerrLock
 */
public class TestIgnoreUnknownParser {

    private final CommandLineParser parser = new IgnoreUnknownParser();

    @Test
    public void test1() throws ParseException {
        Options options = new Options().addOption( "o", "opt", false, "desc" );
        String[] args = {
            "--opt"
        };
        CommandLine commandLine = this.parser.parse( options, args );
        Assert.assertEquals( 1, commandLine.getOptions().length );
    }

    @Test
    public void test2() throws ParseException {
        Options options = new Options().addOption( "o", "opt", false, "desc" );
        String[] args = {
            "--opt", "--unknown"
        };
        CommandLine commandLine = this.parser.parse( options, args );
        Assert.assertEquals( 1, commandLine.getOptions().length );
    }

    @Test
    public void test3() throws ParseException {
        Options options = new Options();
        String[] args = {
            "--opt"
        };
        CommandLine commandLine = this.parser.parse( options, args );
        Assert.assertEquals( 0, commandLine.getOptions().length );
    }

}
