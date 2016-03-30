package de.herrlock.manga.http;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.catalina.LifecycleException;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.exceptions.MDRuntimeException;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;

@SuppressFBWarnings( value = {
    "SE_BAD_FIELD_STORE", "SE_BAD_FIELD", "SE_NO_SERIALVERSIONID"
}, justification = "Don't care about serialization" )
public final class StopServerServlet extends HttpServlet {
    private static final Logger logger = LogManager.getLogger();

    private final Server server;
    private final Runnable shutdownRunnable;

    /**
     * @param server
     */
    StopServerServlet( final Server server ) {
        this.server = server;
        this.shutdownRunnable = new Runnable() {
            @Override
            public void run() {
                try {
                    StopServerServlet.this.server.stop();
                } catch ( LifecycleException ex ) {
                    throw new MDRuntimeException( ex );
                }
            }
        };
    }

    @Override
    protected void doGet( final HttpServletRequest req, final HttpServletResponse res ) throws ServletException, IOException {
        logger.info( "starting new Thread to shutdown the server." );
        new Thread( this.shutdownRunnable ).start();
    }
}
