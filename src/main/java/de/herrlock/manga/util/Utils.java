package de.herrlock.manga.util;

import static de.herrlock.manga.util.log.LogInitializer.L;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public abstract class Utils {
    public static Proxy proxy;
    public static int timeout;

    public static void init( Proxy p, int t ) {
        proxy = p;
        timeout = t;
    }

    public static Document getDocument( URL url ) throws IOException {
        L.debug( "Fetching " + url );
        return proxy == null ? getNormal( url ) : getByProxy( url );
    }

    static Document getNormal( URL url ) throws IOException {
        return Jsoup.parse( url, timeout );
    }

    static Document getByProxy( URL url ) throws IOException {
        URLConnection con = url.openConnection( proxy );
        con.setConnectTimeout( timeout );
        con.setReadTimeout( timeout );
        StringBuilder sb = new StringBuilder();
        try ( BufferedReader reader = new BufferedReader( new InputStreamReader( con.getInputStream(), StandardCharsets.UTF_8 ) ) ) {
            String nextline = null;
            while ( ( nextline = reader.readLine() ) != null ) {
                sb.append( nextline );
            }
        }
        return Jsoup.parse( sb.toString(), url.toExternalForm() );
    }
}
