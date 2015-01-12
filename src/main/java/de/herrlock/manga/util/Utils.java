package de.herrlock.manga.util;

import static de.herrlock.manga.util.log.LogInitializer.L;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public final class Utils {
    public static Document getDocument(URL url) throws IOException {
        L.debug("Fetching " + url);
        return Jsoup.parse(url, 10_000);
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

    public static int getTimeout(Map<String, String> map) {
        try {
            String _timeout = map.get(Constants.PARAM_TIMEOUT);
            if (_timeout != null)
                return Integer.parseInt(_timeout);
        }
        catch (NumberFormatException ex) {
            // do nothing
        }
        return Constants.PARAM_TIMEOUT_DEFAULT;
    }

    private Utils() {
        // not called
    }
}
