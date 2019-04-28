package de.herrlock.manga.http.client;

import static org.junit.Assert.assertEquals;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.Properties;

import org.eclipse.jetty.client.api.Request;
import org.junit.Test;

import de.herrlock.manga.DummyServer;
import de.herrlock.manga.util.configuration.Configuration;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

public class TestJettyClient {

    @Test
    public void testCreateHttpGet() throws MalformedURLException {
        URL url = new URL( "http", "localhost", 1337, "/" );
        Properties p = new Properties();
        p.setProperty( Configuration.URL, url.toExternalForm() );
        DownloadConfiguration conf = DownloadConfiguration.create( p );
        Request request = JettyClient.createHttpGet( url, conf );
        URI uri = request.getURI();
        assertEquals( "http://localhost:1337/", uri.toString() );
    }

    @Test
    public void testExecuteHttpGet() throws Exception {
        DummyServer server = new DummyServer( 1337 );

        URL url = new URL( "http", "localhost", 1337, "/" );
        Properties p = new Properties();
        p.setProperty( Configuration.URL, url.toExternalForm() );
        DownloadConfiguration conf = DownloadConfiguration.create( p );
        ResponseHandler<String> responseHandler = ResponseHandler.fromFunction( input -> input.getContentAsString() );

        server.start();
        String response = JettyClient.getDataAndExecuteResponseHandler( url, conf, responseHandler );
        server.stop();

        assertEquals( 1, server.timesHandlerCalled() );
        assertEquals( "OK\r\n", response );
    }

}
