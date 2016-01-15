package de.herrlock.manga.downloader.dqc;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
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
    private final String expString;
    private final URL pageUrl;
    private final Page page;

    /**
     * @return the parameters to use for the tests
     * @throws MalformedURLException
     */
    @Parameters( name = "{0}" )
    public static Collection<Object[]> getParams() throws MalformedURLException {
        List<Object[]> result = new ArrayList<>();
        for ( int i = 0; i < 20; i++ ) {
            Object[] o = {
                i, ( i < 10 ? "0" : "" ) + i, new URL( "http", "www.example.com", "/" + i )
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
    public TestPage( final int pageNumber, final String expString, final URL pageUrl ) {
        this.expString = expString;
        this.pageUrl = pageUrl;
        this.page = new Page( pageUrl, testfile, pageNumber );
    }

    /**
     * test if the generated file equals the expected value
     */
    @Test
    public void testFiles() {
        File exp = new File( testfile, this.expString + ".jpg" );
        Assert.assertEquals( this.page.getTargetFile(), exp );
    }

    /**
     * Tests the method {@link Page#getUrl()}
     */
    @Test
    public void testGetUrl() {
        Assert.assertEquals( this.pageUrl, this.page.getUrl() );
    }
}
