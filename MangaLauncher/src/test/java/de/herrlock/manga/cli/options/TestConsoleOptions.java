package de.herrlock.manga.cli.options;

import org.apache.commons.cli.Options;
import org.junit.Assert;
import org.junit.Test;

public final class TestConsoleOptions {

    @Test
    public void testOptionCount() {
        ConsoleOptions mainOptions = new ConsoleOptions();
        Options options = mainOptions.getOptions();
        Assert.assertEquals( 6, options.getOptions().size() );
    }

    @Test
    public void testRequiredOptionCount() {
        ConsoleOptions mainOptions = new ConsoleOptions();
        Options options = mainOptions.getOptions();
        Assert.assertEquals( 0, options.getRequiredOptions().size() );
    }

}
