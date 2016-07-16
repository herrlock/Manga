package de.herrlock.log4j2.filter;

import static org.junit.Assert.assertEquals;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.AbstractLogEvent;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.message.SimpleMessage;
import org.junit.Before;
import org.junit.Test;

public final class TestLevelFilter {

    private LevelFilter levelFilter;

    @Before
    public void setUp() {
        this.levelFilter = LevelFilter.createFilter( Level.INFO, Result.ACCEPT, Result.DENY );
    }

    @Test
    public void testFilterLevel() {
        assertEquals( Result.DENY, this.levelFilter.filter( Level.DEBUG ) );
        assertEquals( Result.ACCEPT, this.levelFilter.filter( Level.INFO ) );
    }

    @Test
    public void testSetLevel() {
        assertEquals( Result.DENY, this.levelFilter.filter( Level.DEBUG ) );
        assertEquals( Result.ACCEPT, this.levelFilter.filter( Level.INFO ) );
        this.levelFilter.setLevel( Level.WARN );
        assertEquals( Result.DENY, this.levelFilter.filter( Level.DEBUG ) );
        assertEquals( Result.DENY, this.levelFilter.filter( Level.INFO ) );
        assertEquals( Result.ACCEPT, this.levelFilter.filter( Level.WARN ) );
    }

    @Test
    public void testFilterLogEvent() {
        assertEquals( Result.DENY, this.levelFilter.filter( new SimpleLogEvent( Level.DEBUG ) ) );
        assertEquals( Result.ACCEPT, this.levelFilter.filter( new SimpleLogEvent( Level.INFO ) ) );
    }

    @Test
    public void testFilter1() {
        assertEquals( Result.DENY, this.levelFilter.filter( null, Level.DEBUG, null, "" ) );
        assertEquals( Result.ACCEPT, this.levelFilter.filter( null, Level.INFO, null, "" ) );
    }

    @Test
    public void testFilter2() {
        assertEquals( Result.DENY, this.levelFilter.filter( null, Level.DEBUG, null, new Object(), new DummyException() ) );
        assertEquals( Result.ACCEPT, this.levelFilter.filter( null, Level.INFO, null, new Object(), new DummyException() ) );
    }

    @Test
    public void testFilter3() {
        assertEquals( Result.DENY,
            this.levelFilter.filter( null, Level.DEBUG, null, new SimpleMessage(), new DummyException() ) );
        assertEquals( Result.ACCEPT,
            this.levelFilter.filter( null, Level.INFO, null, new SimpleMessage(), new DummyException() ) );
    }

    @Test
    public void testToString() {
        assertEquals( "LevelFilter: INFO", this.levelFilter.toString() );
    }

    private static final class SimpleLogEvent extends AbstractLogEvent {
        private static final long serialVersionUID = 1L;
        private final Level level;

        public SimpleLogEvent( final Level level ) {
            this.level = level;
        }

        @Override
        public Level getLevel() {
            return this.level;
        }
    }

    private static final class DummyException extends Exception {
        // dummy
    }

}
