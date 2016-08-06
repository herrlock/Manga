package de.herrlock.manga.tomcat.service;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.manga.http.Server;

/**
 * A Servlet that can stop a {@link Server}
 * 
 * @author HerrLock
 */
@Path( "stop" )
public final class StopServerService {
    private static final Logger logger = LogManager.getLogger();

    @GET
    @Produces( MediaType.TEXT_XML )
    public String stopServer() throws IOException {
        logger.info( "Indicating server to shut down." );
        try ( Socket socket = new Socket( "localhost", 1904 ) ) {
            try ( OutputStream out = socket.getOutputStream() ) {
                out.write( "SHUTDOWN".getBytes( StandardCharsets.UTF_8 ) );
            }
        }
        return "<stop success=\"true\" />";
    }

}
