package de.herrlock.manga.util;

import static de.herrlock.manga.util.log.LogInitializer.L;

import java.io.IOException;
import java.net.URL;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

public abstract class Utils {
    public static Document getDocument(URL url) throws IOException {
        L.debug("Fetching " + url);
        return Jsoup.parse(url, 10_000);
    }

}
