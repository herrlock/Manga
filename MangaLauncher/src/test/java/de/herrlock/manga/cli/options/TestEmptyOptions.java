package de.herrlock.manga.cli.options;

import org.apache.commons.cli.Options;
import org.junit.Assert;
import org.junit.Test;

public final class TestEmptyOptions {

    @Test
    public void testOptionCount() {
        EmptyOptions mainOptions = new EmptyOptions();
        Options options = mainOptions.getOptions();
        Assert.assertEquals( 0, options.getOptions().size() );
    }

    @Test
    public void testRequiredOptionCount() {
        EmptyOptions mainOptions = new EmptyOptions();
        Options options = mainOptions.getOptions();
        Assert.assertEquals( 0, options.getRequiredOptions().size() );
    }

}
