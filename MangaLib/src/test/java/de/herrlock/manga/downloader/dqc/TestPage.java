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

import de.herrlock.manga.downloader.dqc.DownloadQueueContainer.Page;

@RunWith( Parameterized.class )
public class TestPage {

    private final int pageNumber;
    private final String expString;

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

    public TestPage( int pageNumber, String expString ) {
        this.pageNumber = pageNumber;
        this.expString = expString;
    }

    @Test
    public void testFiles() {
        File testfile = new File( "./test" );
        Page p = new Page( null, testfile, this.pageNumber );
        File exp = new File( testfile, this.expString + ".jpg" );
        Assert.assertEquals( p.getTargetFile(), exp );
    }
}
