package de.herrlock.manga.util;

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

import org.apache.http.HttpException;
import org.apache.http.HttpRequest;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.bootstrap.HttpServer;
import org.apache.http.impl.bootstrap.ServerBootstrap;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpRequestHandler;
import org.apache.http.util.EntityUtils;
import org.junit.Assert;
import org.junit.Test;

import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.util.configuration.Configuration;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

public class TestUtils {

    @Test
    public void testCreateHttpGet() throws MalformedURLException {
        URL url = new URL( "http", "localhost", 1337, "/" );
        Properties p = new Properties();
        p.setProperty( Configuration.URL, url.toExternalForm() );
        DownloadConfiguration conf = DownloadConfiguration.create( p );
        HttpGet httpGet = Utils.createHttpGet( url, conf );
        URI uri = httpGet.getURI();
        Assert.assertEquals( "http://localhost:1337/", uri.toString() );
    }

    @Test
    public void testExecuteHttpGet() throws ClientProtocolException, IOException {
        final String MAGIC_SUPER_SECRET_STRING = "somethingRandom";
        HttpRequestHandler requestHandler = new SetStringHttpRequestHandler( MAGIC_SUPER_SECRET_STRING );
        HttpServer server = ServerBootstrap.bootstrap().setListenerPort( 1337 ).registerHandler( "/", requestHandler ).create();

        URL url = new URL( "http", "localhost", 1337, "/" );
        Properties p = new Properties();
        p.setProperty( Configuration.URL, url.toExternalForm() );
        DownloadConfiguration conf = DownloadConfiguration.create( p );
        ResponseHandler<Boolean> responseHandler = new ResponseEqualsStringResponseHandler( MAGIC_SUPER_SECRET_STRING );

        server.start();
        Utils.getDataAndExecuteResponseHandler( url, conf, responseHandler );
        server.stop();
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

    private static final class SetStringHttpRequestHandler implements HttpRequestHandler {
        private final String stringtoSet;

        private SetStringHttpRequestHandler( String stringToSet ) {
            this.stringtoSet = stringToSet;
        }
        @Override
        public void handle( HttpRequest request, HttpResponse response, HttpContext context ) throws HttpException, IOException {
            response.setEntity( new StringEntity( this.stringtoSet ) );
        }
    }

    private static final class ResponseEqualsStringResponseHandler implements ResponseHandler<Boolean> {
        private final String stringToTest;

        private ResponseEqualsStringResponseHandler( String stringToTest ) {
            this.stringToTest = stringToTest;
        }
        @Override
        public Boolean handleResponse( HttpResponse response ) throws ClientProtocolException, IOException {
            String readString = EntityUtils.toString( response.getEntity() );
            return this.stringToTest.equals( readString );
        }
    }

    private static final class BoolRunnable implements Runnable {
        private boolean ran = false;

        @Override
        public void run() {
            this.ran = true;
        }
    }

    private static final class BoolCallable implements Callable<Boolean> {
        @Override
        public Boolean call() throws Exception {
            return Boolean.TRUE;
        }
    }

    /**
     * A Runnable and Callable that sleeps for n seconds
     */
    private static final class SleepingRunnable implements Runnable, Callable<Void> {
        private final int time;

        public SleepingRunnable( int time ) {
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
        public Void call() throws Exception {
            this.run();
            return null;
        }
    }

    /**
     * A Runnable that interrupts a given Thread
     */
    private static final class InterruptingRunnable implements Runnable, Callable<Void> {
        private final Thread toInterrupt;

        public InterruptingRunnable( Thread toInterrupt ) {
            this.toInterrupt = toInterrupt;
        }

        @Override
        public void run() {
            this.toInterrupt.interrupt();
        }

        @Override
        public Void call() throws Exception {
            this.run();
            return null;
        }
    }
}
