package de.herrlock.manga;

import org.apache.commons.cli.Options;
import org.junit.Assert;
import org.junit.Test;

public final class TestMyOptions {

    @Test
    public void testOptionCount() {
        MyOptions myOptions = new MyOptions();
        Options options = myOptions.getOptions();
        Assert.assertEquals( 12, options.getOptions().size() );
    }

    @Test
    public void testRequiredOptionCount() {
        MyOptions myOptions = new MyOptions();
        Options options = myOptions.getOptions();
        Assert.assertEquals( 1, options.getRequiredOptions().size() );
    }

}
