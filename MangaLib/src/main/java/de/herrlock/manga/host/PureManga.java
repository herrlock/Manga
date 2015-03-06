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

class PureManga extends ChapterList {
    private static final long serialVersionUID = 1L;

    private final String name;

    public PureManga( URL url ) throws IOException {
        super();
        Document document = Utils.getDocument( url );

        this.name = document.select( "#content h2.titlebg" ).first().text();

        Elements tr = document.getElementsByClass( "element" );
        for ( Element e : tr ) {
            Element link = e.select( "div>a" ).get( 0 );

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
        String src = Utils.getDocument( url ).select( "#page>.inner>a>img" ).get( 0 ).attr( "src" );
        return new URL( url, src );
    }

    @Override
    protected Map<Integer, URL> _getAllPageURLs( URL url ) throws IOException {
        Map<Integer, URL> result = new ConcurrentHashMap<>();
        Elements li = Utils.getDocument( url ).select( ".dropdown_right>ul>li" );
        for ( Element e : li ) {
            Element link = e.getElementsByTag( "a" ).get( 0 );
            int number = Integer.parseInt( link.text().substring( 6 ) );
            URL absUrl = new URL( url, link.attr( "href" ) );
            result.put( number, absUrl );
        }
        return result;
    }

}
