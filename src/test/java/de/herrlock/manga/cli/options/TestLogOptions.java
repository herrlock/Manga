package de.herrlock.manga.cli.options;

import org.apache.commons.cli.Options;
import org.junit.Assert;
import org.junit.Test;

public final class TestLogOptions {

    @Test
    public void testOptionCount() {
        LogOptions mainOptions = new LogOptions();
        Options options = mainOptions.getOptions();
        Assert.assertEquals( 3, options.getOptions().size() );
    }

    @Test
    public void testRequiredOptionCount() {
        LogOptions mainOptions = new LogOptions();
        Options options = mainOptions.getOptions();
        Assert.assertEquals( 0, options.getRequiredOptions().size() );
    }

}
