package de.herrlock.manga.downloader.dqc;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Test the class Page
 * 
 * @author HerrLock
 */
@RunWith( Parameterized.class )
public class TestPage {

    private static final File testfile = new File( "./test" );
    private final int pageNumber;
    private final String expString;

    /**
     * @return the parameters to use for the tests
     */
    @Parameters( name = "{0}" )
    public static Collection<Object[]> getParams() {
        List<Object[]> result = new ArrayList<>();
        for ( int i = 0; i < 20; i++ ) {
            Object[] o = {
                i, ( i < 10 ? "0" : "" ) + i
            };
            result.add( o );
        }
        return result;
    }

    /**
     * @param pageNumber
     *            the page's number
     * @param expString
     *            the expected filename
     */
    public TestPage( final int pageNumber, final String expString ) {
        this.pageNumber = pageNumber;
        this.expString = expString;
    }

    /**
     * test if the generated file equals the expected value
     */
    @Test
    public void testFiles() {
        Page p = new Page( null, testfile, this.pageNumber );
        File exp = new File( testfile, this.expString + ".jpg" );
        Assert.assertEquals( p.getTargetFile(), exp );
    }
}
