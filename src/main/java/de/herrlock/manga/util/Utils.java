package de.herrlock.manga.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import de.herrlock.log.Logger;
import de.herrlock.manga.connection.ConnectionFactory;
import de.herrlock.manga.connection.DirectConnectionFactory;
import de.herrlock.manga.connection.ProxyConnectionFactory;

public final class Utils {

    private static Map<String, String> arguments;
    private static ConnectionFactory conFactory;
    private static Logger log;

    public static Map<String, String> getArguments() {
        if (arguments == null)
            throw new RuntimeException("arguments not yet initialized");
        return arguments;
    }

    public static void setArguments(Map<String, String> m) {
        arguments = Collections.unmodifiableMap(m);
        log = Logger.getLogger(arguments.get(Constants.PARAM_LOGLEVEL));

        String host = m.get(Constants.PARAM_PROXY_HOST);
        String port = m.get(Constants.PARAM_PROXY_PORT);
        if (host != null && !"".equals(host) && port != null && !"".equals(port)) {
            String timeout = m.get(Constants.PARAM_TIMEOUT);
            conFactory = new ProxyConnectionFactory(timeout, host, port);
        }
        else {
            conFactory = new DirectConnectionFactory(port);
        }
    }

    public static Logger getLogger() {
        return log;
    }

    public static URLConnection getConnection(URL url) throws IOException {
        return conFactory.getConnection(url);
    }

    public static Document getDocument(URL url) throws IOException {
        Utils.getLogger().debug("Fetching " + url);
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

    public static URL getMangaURL() {
        try {
            String _url = arguments.get(Constants.PARAM_URL);
            if (!_url.startsWith("http"))
                _url = "http://" + _url;
            return new URL(_url);
        }
        catch (MalformedURLException ex) {
            throw new RuntimeException(ex);
        }
    }

    public static String getPattern() {
        return arguments.get(Constants.PARAM_PATTERN);
    }

    private Utils() {
        // not called
    }

}
