package de.herrlock.manga.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Collection;

import org.junit.Test;

import de.herrlock.manga.util.ChapterPattern.Interval;

/**
 * Test the behaviour of the class {@link ChapterPattern}
 * 
 * @author HerrLock
 */
public class TestChapterPattern {

    /**
     * Test the "minus"-operator, all numbers between should be included
     */
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

    /**
     * Test the "semicolon"-operator, these numbers should be included
     */
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

    /**
     * Test a combination of "minus" and "semicolon"
     */
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

    /**
     * checks if non-integer values are included correctly
     */
    @Test
    public void testComma() {
        ChapterPattern cp = new ChapterPattern( "5.5" );
        assertTrue( cp.contains( "5.5" ) );
    }

    /**
     * tests if an invalid pattern results in an empty List of values
     * 
     * @throws ReflectiveOperationException
     *             thrown by the methods {@link Class#getDeclaredField(String)} and {@link Field#get(Object)}, should not occur
     */
    @Test
    public void testNull() throws ReflectiveOperationException {
        ChapterPattern cp = new ChapterPattern( null );
        Field declaredField = cp.getClass().getDeclaredField( "elements" );
        declaredField.setAccessible( true );
        @SuppressWarnings( "unchecked" )
        Collection<Interval> c = ( Collection<Interval> ) declaredField.get( cp );
        assertTrue( "a ChapterPattern with null as arguments must not create elements", c.isEmpty() );
    }

    /**
     * tests if an invalid pattern results in an empty List of values
     * 
     * @throws ReflectiveOperationException
     *             thrown by the methods {@link Class#getDeclaredField(String)} and {@link Field#get(Object)}, should not occur
     */
    @Test
    public void testInvalid() throws ReflectiveOperationException {
        ChapterPattern cp = new ChapterPattern( "10;" );
        Field declaredField = cp.getClass().getDeclaredField( "elements" );
        declaredField.setAccessible( true );
        @SuppressWarnings( "unchecked" )
        Collection<Interval> c = ( Collection<Interval> ) declaredField.get( cp );
        assertTrue( "an invalid CP must not create elements", c.isEmpty() );
    }

    /**
     * Test the toString-method
     * 
     * @throws ReflectiveOperationException
     */
    @Test
    public void testToString() throws ReflectiveOperationException {
        ChapterPattern cp = new ChapterPattern( "10;" );
        Field declaredField = cp.getClass().getDeclaredField( "elements" );
        declaredField.setAccessible( true );
        @SuppressWarnings( "unchecked" )
        Collection<Interval> c = ( Collection<Interval> ) declaredField.get( cp );
        assertEquals( c.toString(), cp.toString() );
    }

    /**
     * Tests the class Interval
     */
    @Test
    public void testIntervalWith2Params() {
        Interval i = new Interval( "13", "15" );
        assertTrue( i.contains( "13" ) );
        assertTrue( i.contains( "14" ) );
        assertTrue( i.contains( "15" ) );
    }

    /**
     * Tests the class Interval
     */
    @Test
    public void testIntervalWith1Argument() {
        Interval i = new Interval( "42" );
        // contains these
        assertTrue( i.contains( "42" ) );
        // does not contain these
        assertFalse( i.contains( "41" ) );
        assertFalse( i.contains( "43" ) );
    }

    /**
     * Test a {@code null} argument for Interval.contains(String)
     */
    @Test
    public void testIntervalContainsNull_S() {
        Interval i = new Interval( "42" );
        assertFalse( i.contains( ( String ) null ) );
    }

    /**
     * Test a {@code null} argument for Interval.contains(BigDecimal)
     */
    @Test
    public void testIntervalContainsNull_BD() {
        Interval i = new Interval( "42" );
        assertFalse( i.contains( ( BigDecimal ) null ) );
    }

    /**
     * Test Interval.toString()
     */
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
