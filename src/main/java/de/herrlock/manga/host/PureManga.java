package de.herrlock.manga.host;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.herrlock.manga.util.Utils;

class PureManga extends ChapterList
{
    private static final long serialVersionUID = 1L;

    public PureManga(URL url, String chapterPattern) throws IOException
    {
        super(chapterPattern);
        Elements tr = Utils.getDocument(url).getElementsByClass("element");
        for (Element e : tr)
        {
            Element link = e.select("div>a").get(0);

            String[] chapterNrAndName = link.text().split(":");
            String number = chapterNrAndName[0].split(" ")[1];

            URL chapterUrl = new URL(url, link.attr("href"));

            super.addChapter(number, chapterUrl);
        }
        Collections.reverse(this);
    }

    @Override
    public String imgLink(URL url) throws IOException
    {
        return Utils.getDocument(url).select("#page>.inner>a>img").get(0).attr("src");
    }

    @Override
    public Map<Integer, URL> getAllPageURLs(URL url) throws IOException
    {
        Map<Integer, URL> result = new HashMap<>();
        Elements li = Utils.getDocument(url).select(".dropdown_right>ul>li");
        for (Element e : li)
        {
            Element link = e.getElementsByTag("a").get(0);
            int number = Integer.parseInt(link.text().substring(6));
            URL absUrl = new URL(url, link.attr("href"));
            result.put(number, absUrl);
        }
        return Collections.unmodifiableMap(result);
    }

}
