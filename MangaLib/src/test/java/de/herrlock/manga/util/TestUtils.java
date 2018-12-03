package de.herrlock.manga.util;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import org.eclipse.jetty.client.api.ContentResponse;
import org.eclipse.jetty.client.api.Request;
import org.junit.Assert;
import org.junit.Test;

import de.herrlock.manga.DummyServer;
import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.util.Utils.ResponseHandler;
import de.herrlock.manga.util.configuration.Configuration;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

public class TestUtils {

    @Test
    public void testCreateHttpGet() throws MalformedURLException {
        URL url = new URL( "http", "localhost", 1337, "/" );
        Properties p = new Properties();
        p.setProperty( Configuration.URL, url.toExternalForm() );
        DownloadConfiguration conf = DownloadConfiguration.create( p );
        Request request = Utils.createHttpGet( url, conf );
        URI uri = request.getURI();
        Assert.assertEquals( "http://localhost:1337/", uri.toString() );
    }

    @Test
    public void testExecuteHttpGet() throws Exception {
        DummyServer server = new DummyServer( 1337 );

        URL url = new URL( "http", "localhost", 1337, "/" );
        Properties p = new Properties();
        p.setProperty( Configuration.URL, url.toExternalForm() );
        DownloadConfiguration conf = DownloadConfiguration.create( p );
        ResponseHandler<String> responseHandler = new ResponseHandler<String>() {
            @Override
            protected String handle( final ContentResponse input ) throws IOException {
                return input.getContentAsString();
            }
        };

        server.start();
        String response = Utils.getDataAndExecuteResponseHandler( url, conf, responseHandler );
        server.stop();

        assertEquals( 1, server.timesHandlerCalled() );
        assertEquals( "OK\r\n", response );
    }

    @SuppressWarnings( "deprecation" )
    @Test
    public void testRunThreads() {
        BoolRunnable runnable1 = new BoolRunnable();
        BoolRunnable runnable2 = new BoolRunnable();
        Collection<? extends Thread> threads = Arrays.asList( new Thread( runnable1 ), new Thread( runnable2 ) );
        Utils.startAndWaitForThreads( threads );
        Assert.assertTrue( runnable1.ran );
        Assert.assertTrue( runnable2.ran );
    }

    @SuppressWarnings( "deprecation" )
    @Test( expected = MDRuntimeException.class )
    public void testRunThreadsInterrupted() {
        Thread runningThread = new Thread( new SleepingRunnable( -1 ) );
        Thread interruptThread = new Thread( new InterruptingRunnable( Thread.currentThread() ) );
        Collection<? extends Thread> threads = Arrays.asList( runningThread, interruptThread );
        Utils.startAndWaitForThreads( threads );
        Assert.fail( "should not get here" );
    }

    @Test
    public void testCallCallables() throws InterruptedException, ExecutionException {
        BoolCallable callable1 = new BoolCallable();
        BoolCallable callable2 = new BoolCallable();
        Collection<? extends Callable<Boolean>> callables = Arrays.asList( callable1, callable2 );
        List<Future<Boolean>> futures = Utils.callCallables( callables );
        for ( Future<Boolean> future : futures ) {
            Assert.assertEquals( Boolean.TRUE, future.get() );
        }
    }

    @Test( expected = MDRuntimeException.class )
    public void testCallCallablesInterrupted() {
        Callable<Void> runningCallable = new SleepingRunnable( -1 );
        Callable<Void> interruptCallable = new InterruptingRunnable( Thread.currentThread() );
        Collection<? extends Callable<Void>> callables = Arrays.asList( runningCallable, interruptCallable );
        Utils.callCallables( callables );
        Assert.fail( "should not get here" );
    }

    private static final class BoolRunnable implements Runnable {
        private boolean ran;

        @Override
        public void run() {
            this.ran = true;
        }
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
