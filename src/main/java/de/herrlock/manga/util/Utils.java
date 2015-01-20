package de.herrlock.manga.util;

import static de.herrlock.manga.util.log.LogInitializer.L;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.herrlock.manga.connection.ConnectionFactory;
import de.herrlock.manga.connection.DirectConnectionFactory;
import de.herrlock.manga.connection.ProxyConnectionFactory;
import de.herrlock.manga.downloader.MDownloader;

public final class Utils {
    private static ConnectionFactory conFactory;
    static {
        String timeout = MDownloader.arguments.get(Constants.PARAM_TIMEOUT);
        String host = MDownloader.arguments.get(Constants.PARAM_PROXY_HOST);
        String port = MDownloader.arguments.get(Constants.PARAM_PROXY_PORT);
        if (host != null && !"".equals(host) && port != null && !"".equals(port)) {
            conFactory = new ProxyConnectionFactory(timeout, host, port);
        }
        else {
            conFactory = new DirectConnectionFactory(port);
        }
    }

    public static URLConnection getConnection(URL url) throws IOException {
        return conFactory.getConnection(url);
    }

    public static Document getDocument(URL url) throws IOException {
        L.debug("Fetching " + url);
        URLConnection con = getConnection(url);
        List<String> list = readStream(con.getInputStream());
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            sb.append(s);
        }
        return Jsoup.parse(sb.toString());
    }

    public static List<String> readStream(InputStream in) throws IOException {
        List<String> list = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8))) {
            String nextline = null;
            while ((nextline = reader.readLine()) != null) {
                list.add(nextline);
            }
        }
        return list;
    }

    public static URL getURL() {
        try {
            String _url = MDownloader.arguments.get(Constants.PARAM_URL);
            if (!_url.startsWith("http"))
                _url = "http://" + _url;
            return new URL(_url);
        }
        catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getPattern() {
        return MDownloader.arguments.get(Constants.PARAM_PATTERN);
    }

    private Utils() {
        // not called
    }

}
