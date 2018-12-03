package de.herrlock.manga;

import java.io.IOException;
import java.util.concurrent.atomic.AtomicInteger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.AbstractHandler;

/**
 * @author HerrLock
 */
public class DummyServer {
    private final Server server;
    private final DummyDefaultHandler handler;

    public DummyServer( final int port ) {
        this.server = new Server( port );
        this.handler = new DummyDefaultHandler();
        this.server.setHandler( this.handler );
    }

    public int timesHandlerCalled() {
        return this.handler.timesCalled();
    }

    public final void start() throws Exception {
        this.server.start();
    }

    public final void stop() throws Exception {
        this.server.stop();
    }

    private static final class DummyDefaultHandler extends AbstractHandler {
        private final AtomicInteger called = new AtomicInteger();

        @Override
        public void handle( final String target, final Request baseRequest, final HttpServletRequest request,
            final HttpServletResponse response ) throws IOException, ServletException {
            this.called.incrementAndGet();
            response.setStatus( HttpServletResponse.SC_OK );
            response.getWriter().write( "OK\r\n" );
            baseRequest.setHandled( true );
        }

        public int timesCalled() {
            return this.called.get();
        }

    }
}
