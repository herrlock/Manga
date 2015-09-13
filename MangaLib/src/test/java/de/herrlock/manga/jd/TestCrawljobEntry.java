package de.herrlock.manga.jd;

import org.junit.Assert;
import org.junit.Test;

/**
 * @author HerrLock
 */
public class TestCrawljobEntry {

    /**
     * Test the method CrawljobEntry.export()
     */
    @Test
    public void testExport() {
        CrawljobEntry crawljobEntry = new CrawljobEntry( "filename", "url" );
        String export = crawljobEntry.export();
        String[] split = export.split( System.lineSeparator() );
        String[] expected = {
            "text=url", "filename=filename.jpg"
        };
        Assert.assertArrayEquals( expected, split );
    }
}
