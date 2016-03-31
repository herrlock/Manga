package de.herrlock.manga.util;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

/**
 * Tests the ChapterPattern's matcher-regex
 * 
 * @author HerrLock
 */
@RunWith( Parameterized.class )
public class TestChapterPatternRegex {
    private final String toCheck;
    private final boolean expected;

    /**
     * @param toCheck
     *            the value to check
     * @param expected
     *            if the value to check is expected to be valid
     */
    public TestChapterPatternRegex( final String toCheck, final boolean expected ) {
        this.toCheck = toCheck;
        this.expected = expected;
    }

    /**
     * tests a single entry
     */
    @Test
    public void testCase() {
        assertEquals( this.expected, ChapterPattern.REGEX.matcher( this.toCheck ).matches() );
    }

    /**
     * Creates the entries to test
     * 
     * @return the paramters
     */
    @Parameters( name = "{0} => {1}" )
    public static Collection<Object[]> generateParams() {
        Object[][] params = {
            {
                "10", true
            }, {
                "10-12", true
            }, {
                "10;11", true
            }, {
                "10;11-13", true
            }, {
                "10;", false
            }, {
                "10;;11", false
            }
        };
        return Arrays.asList( params );
    }
}
