package de.herrlock.manga.cli.options;

import org.apache.commons.cli.Options;
import org.junit.Assert;
import org.junit.Test;

public final class TestMainOptions {

    @Test
    public void testOptionCount() {
        MainOptions mainOptions = new MainOptions();
        Options options = mainOptions.getOptions();
        Assert.assertEquals( 6, options.getOptions().size() );
    }

    @Test
    public void testRequiredOptionCount() {
        MainOptions mainOptions = new MainOptions();
        Options options = mainOptions.getOptions();
        Assert.assertEquals( 1, options.getRequiredOptions().size() );
    }

}
