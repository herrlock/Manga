package de.herrlock.manga.util;

import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.junit.Test;

import de.herrlock.manga.exceptions.MDRuntimeException;

public class TestUtils {

    @Test
    public void testCallCallables() throws InterruptedException, ExecutionException {
        BoolCallable callable1 = new BoolCallable();
        BoolCallable callable2 = new BoolCallable();
        Collection<? extends Callable<Boolean>> callables = Arrays.asList( callable1, callable2 );
        List<Future<Boolean>> futures = Utils.callCallables( callables );
        for ( Future<Boolean> future : futures ) {
            assertTrue( future.get() );
        }
    }

    @Test( expected = MDRuntimeException.class )
    public void testCallCallablesInterrupted() {
        Callable<Void> runningCallable = new SleepingRunnable( -1 );
        Callable<Void> interruptCallable = new InterruptingRunnable( Thread.currentThread() );
        Collection<? extends Callable<Void>> callables = Arrays.asList( runningCallable, interruptCallable );
        Utils.callCallables( callables );
        fail( "should not get here" );
    }

    private static final class BoolCallable implements Callable<Boolean> {
        @Override
        public Boolean call() {
            return Boolean.TRUE;
        }
    }

    /**
     * A Runnable and Callable that sleeps for n seconds
     */
    private static final class SleepingRunnable implements Runnable, Callable<Void> {
        private final int time;

        public SleepingRunnable( final int time ) {
            this.time = time < 0 ? 10_000 : time;
        }

        @Override
        public void run() {
            try {
                Thread.sleep( this.time );
            } catch ( InterruptedException ex ) {
                throw new MDRuntimeException( ex );
            }
        }

        @Override
        public Void call() {
            this.run();
            return null;
        }
    }

    /**
     * A Runnable that interrupts a given Thread
     */
    private static final class InterruptingRunnable implements Runnable, Callable<Void> {
        private final Thread toInterrupt;

        public InterruptingRunnable( final Thread toInterrupt ) {
            this.toInterrupt = toInterrupt;
        }

        @Override
        public void run() {
            this.toInterrupt.interrupt();
        }

        @Override
        public Void call() {
            this.run();
            return null;
        }
    }
}
