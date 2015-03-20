package de.herrlock.manga.jd;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

public class JDConnector {
    public static void main( String[] args ) throws IOException {
        JDConnector jdc = new JDConnector( "somePackage", "someDir" );
        jdc.addLink( new URL( "http://a.mfcdn.net/store/manga/13229/01-000.0/compressed/d001.jpg" ) );
        jdc.addLink( new URL( "http://a.mfcdn.net/store/manga/13229/01-000.0/compressed/d002.jpg" ) );
        jdc.postLinks();
    }

    private final List<URL> urls = new ArrayList<>();
    private final String packageName, dirName;

    public JDConnector( String packageName, String dirName ) {
        this.packageName = packageName;
        this.dirName = dirName;
        try {
            URL flash = new URL( "http://127.0.0.1:9666/flash/" );
            URLConnection con = flash.openConnection();
            try ( BufferedReader reader = new BufferedReader(
                new InputStreamReader( con.getInputStream(), StandardCharsets.UTF_8 ) ) ) {
                String readLine = reader.readLine();
                if ( !"JDownloader".equals( readLine ) ) {
                    throw new IllegalStateException( "JDownloader must be running" );
                }
            }
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }
    public void addLink( URL url ) {
        this.urls.add( url );
    }

    public void postLinks() throws IOException {
        if ( !this.urls.isEmpty() ) {
            StringBuilder sb = new StringBuilder( this.urls.remove( 0 ).toExternalForm() );
            for ( URL url : this.urls ) {
                sb.append( "\\r\\n" + url.toExternalForm() );
            }
            this.urls.clear();

            Connection con = Jsoup.connect( "http://127.0.0.1:9666/flash/add" );
            con.data( "urls", sb.toString() );
            con.data( "package", this.packageName );
            con.data( "dir", this.dirName );
            con.ignoreContentType( true );
            con.post();
        }
    }
}
