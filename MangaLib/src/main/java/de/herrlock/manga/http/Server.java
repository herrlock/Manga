package de.herrlock.manga.http;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author HerrLock
 */
public class Server extends ServerSocket implements Runnable {

    public static void main( String[] args ) {

        try ( Server server = new Server() ) {
            Thread serverThread = new Thread( server );
            serverThread.start();

            serverThread.join();
        } catch ( IOException | InterruptedException ex ) {
            throw new RuntimeException( ex );
        }
    }

    public Server() throws IOException {
        super( 1905 );
    }

    @Override
    public void run() {
        try {
            System.out.println( "started" );
            while ( true ) {
                Socket socket = this.accept();
                System.out.println( socket );
                try ( BufferedReader reader = new BufferedReader( new InputStreamReader( socket.getInputStream(),
                    StandardCharsets.UTF_8 ) ) ) {
                    URL url = new URL( "http://localhost" + reader.readLine().split( " " )[1] );
                    Response result = handleXHR( url );
                    try ( BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( socket.getOutputStream(),
                        StandardCharsets.UTF_8 ) ) ) {
                        writer.write( result.toString() );
                    }
                }
            }
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }

    public Response handleXHR( URL url ) {
        String path = url.getPath();
        System.out.println( "Path: " + path );
        String query = url.getQuery();
        System.out.println( "Query: " + query );
        switch ( path.substring( 1 ) ) {
            case "md":
                return createDefaultPage();
            case "add":
                return processQuery( url.getQuery().split( "&" ) );
            default:
                return create404();
        }
    }

    public Response createDefaultPage() {
        Response response = new Response();
        try ( InputStream indexhtml = Server.class.getResourceAsStream( "index.html" ) ) {
            Document document = Jsoup.parse( indexhtml, null, "http://localhost" );
            response.setCode( 200 ).setDocument( document );
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
        return response;
    }

    private Response processQuery( String[] querys ) {
        Map<String, String> params = new HashMap<>( querys.length );
        for ( String param : querys ) {
            String[] paramArr = param.split( "=" );
            params.put( paramArr[0], paramArr[1] );
        }
        // TODO: this function
        return null;
    }

    public Response create404() {
        Document doc = Document.createShell( "localhost" );
        Element head = doc.select( "head" ).first();
        head.appendElement( "title" ).text( "Not Found (404)" );
        Element body = doc.select( "body" ).first();
        body.appendElement( "h1" ).text( "404" );
        body.appendElement( "div" ).text( "Not Found" );

        return new Response( 404, doc );
    }

}
