package de.herrlock.manga.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;

import org.junit.Test;

import de.herrlock.manga.util.ChapterPattern.Interval;

public class TestChapterPattern {

    @Test
    public void testMinus() {
        ChapterPattern cp = new ChapterPattern( "10-12" );
        // contains these
        assertTrue( cp.contains( "10" ) );
        assertTrue( cp.contains( "11" ) );
        assertTrue( cp.contains( "12" ) );
        // does not contain these
        assertFalse( cp.contains( "9" ) );
        assertFalse( cp.contains( "13" ) );
    }

    @Test
    public void testSemicolon() {
        ChapterPattern cp = new ChapterPattern( "10;12;14" );
        // contains these
        assertTrue( cp.contains( "10" ) );
        assertTrue( cp.contains( "12" ) );
        assertTrue( cp.contains( "14" ) );
        // does not contain these
        assertFalse( cp.contains( "11" ) );
        assertFalse( cp.contains( "13" ) );
    }

    @Test
    public void testCombined() {
        ChapterPattern cp = new ChapterPattern( "10-12;14" );
        // contains these
        assertTrue( cp.contains( "10" ) );
        assertTrue( cp.contains( "11" ) );
        assertTrue( cp.contains( "12" ) );
        assertTrue( cp.contains( "14" ) );
        // does not contain these
        assertFalse( cp.contains( "13" ) );
    }

    @Test
    public void testComma() {
        ChapterPattern cp = new ChapterPattern( "5.5" );
        assertTrue( cp.contains( "5.5" ) );
    }

    @Test
    public void testInvalid() throws ReflectiveOperationException {
        ChapterPattern cp = new ChapterPattern( "10;" );
        Field declaredField = cp.getClass().getDeclaredField( "elements" );
        declaredField.setAccessible( true );
        @SuppressWarnings( "unchecked" )
        Collection<Interval> c = ( Collection<Interval> ) declaredField.get( cp );
        assertTrue( "an invalid CP must not create elements", c.isEmpty() );
    }

    @Test
    public void testIntervalWith2Params() {
        Interval i = new Interval( "13", "15" );
        assertTrue( i.contains( "13" ) );
        assertTrue( i.contains( "14" ) );
        assertTrue( i.contains( "15" ) );
    }

    @Test
    public void testIntervalWith1Argument() {
        Interval i = new Interval( "42" );
        // contains these
        assertTrue( i.contains( "42" ) );
        // does not contain these
        assertFalse( i.contains( "41" ) );
        assertFalse( i.contains( "43" ) );
    }

    @Test( expected = IllegalArgumentException.class )
    public void testIntervalWith0Params() {
        Interval i = new Interval();
        assertFalse( i.contains( "42" ) );
        fail( "should not get here" );
    }

    @Test
    public void testIntervalContainsNull() {
        Interval i = new Interval( "42" );
        assertFalse( i.contains( ( BigDecimal ) null ) );
    }

    @Test
    public void testIntervalToString() {
        Interval i1 = new Interval( "13", "15" );
        String string1 = i1.toString();
        String[] split1 = string1.split( " - " );
        assertTrue( split1[0].contains( "13" ) );
        assertTrue( split1[1].contains( "15" ) );

        Interval i2 = new Interval( "42" );
        String string2 = i2.toString();
        String[] split2 = string2.split( " - " );
        assertTrue( split2[0].contains( "42" ) );
        assertTrue( split2[1].contains( "42" ) );
    }
}
