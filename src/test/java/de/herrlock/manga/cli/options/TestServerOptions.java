package de.herrlock.manga.cli.options;

import org.apache.commons.cli.Options;
import org.junit.Assert;
import org.junit.Test;

public final class TestServerOptions {

    @Test
    public void testOptionCount() {
        ServerOptions mainOptions = new ServerOptions();
        Options options = mainOptions.getOptions();
        Assert.assertEquals( 1, options.getOptions().size() );
    }

    @Test
    public void testRequiredOptionCount() {
        ServerOptions mainOptions = new ServerOptions();
        Options options = mainOptions.getOptions();
        Assert.assertEquals( 0, options.getRequiredOptions().size() );
    }

}
