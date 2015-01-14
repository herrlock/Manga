package de.herrlock.manga.util;

import static de.herrlock.manga.util.log.LogInitializer.L;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public final class Utils {
    public static Proxy proxy = Proxy.NO_PROXY;
    public static int timeout = Constants.PARAM_TIMEOUT_DEFAULT;

    public static Document getDocument(URL url) throws IOException {
        L.debug("Fetching " + url);
        URLConnection con = getConnection(url);
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream(), StandardCharsets.UTF_8))) {
            String nextline = null;
            while ((nextline = reader.readLine()) != null) {
                sb.append(nextline);
            }
        }
        return Jsoup.parse(sb.toString());
    }

    public static URLConnection getConnection(URL url) throws IOException {
        URLConnection con = proxy == null ? url.openConnection() : url.openConnection(proxy);
        con.setConnectTimeout(timeout);
        con.setReadTimeout(timeout);
        return con;
    }

    public static URL getURL(Map<String, String> map) {
        try {
            String _url = map.get(Constants.PARAM_URL);
            if (!_url.startsWith("http"))
                _url = "http://" + _url;
            return new URL(_url);
        }
        catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static void setTimeout(Map<String, String> map) {
        try {
            String _timeout = map.get(Constants.PARAM_TIMEOUT);
            if (_timeout != null)
                timeout = Integer.parseInt(_timeout);
        }
        catch (NumberFormatException ex) {
            // do nothing, default value is set previously
        }
    }

    private Utils() {
        // not called
    }
}
