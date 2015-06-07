package de.herrlock.manga.host;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.herrlock.manga.util.Utils;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

final class PureManga extends ChapterList {
    private static final long serialVersionUID = 1L;

    private final String name;

    public PureManga( URL url, DownloadConfiguration conf ) throws IOException {
        super( conf );
        Document document = Utils.getDocument( url, conf );

        this.name = document.select( "#content h2.titlebg" ).first().text();

        Elements tr = document.getElementsByClass( "element" );
        for ( Element e : tr ) {
            Element link = e.select( "div>a" ).first();

            String[] chapterNrAndName = link.text().split( ":" );
            String number = chapterNrAndName[0].split( " " )[1];

            URL chapterUrl = new URL( url, link.attr( "href" ) );

            super.addChapter( number, chapterUrl );
        }
        Collections.reverse( this );
    }

    @Override
    public String getMangaName() {
        return this.name;
    }

    @Override
    public URL imgLink( URL url ) throws IOException {
        String src = Utils.getDocument( url, this.conf ).select( "#page>.inner>a>img" ).first().attr( "src" );
        return new URL( url, src );
    }

    @Override
    protected Map<Integer, URL> _getAllPageURLs( URL url ) throws IOException {
        Map<Integer, URL> result = new ConcurrentHashMap<>();
        Elements li = Utils.getDocument( url, this.conf ).select( ".dropdown_right>ul>li" );
        for ( Element e : li ) {
            Element link = e.getElementsByTag( "a" ).first();
            int number = Integer.parseInt( link.text().substring( 6 ) );
            URL absUrl = new URL( url, link.attr( "href" ) );
            result.put( number, absUrl );
        }
        return result;
    }

}
