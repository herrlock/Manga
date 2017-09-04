package de.herrlock.manga.cli.options;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameter;
import org.junit.runners.Parameterized.Parameters;

@RunWith( Parameterized.class )
public final class TestSubOptions {

    @Parameter( 0 )
    public String subOptionName;
    @Parameter( 1 )
    public Class<? extends SubOptions> expectedClass;

    @Test
    public void testOptionCount() {
        SubOptions subOptions = SubOptions.getSubOptions( this.subOptionName );
        Assert.assertTrue( this.expectedClass.isAssignableFrom( subOptions.getClass() ) );
    }

    @Parameters( name = "{0}: {1}" )
    public static Collection<Object[]> createParams() {
        return Arrays.asList( new Object[][] {
            {
                null, EmptyOptions.class
            }, {
                "console", ConsoleOptions.class
            }, {
                "dialog", EmptyOptions.class
            }, {
                "gui", EmptyOptions.class
            }, {
                "viewpage", ViewpageOptions.class
            }, {
                "server", ServerOptions.class
            }, {
                "help", EmptyOptions.class
            }, {
                "version", EmptyOptions.class
            }, {
                "unknown", EmptyOptions.class
            }
        } );
    }

}
