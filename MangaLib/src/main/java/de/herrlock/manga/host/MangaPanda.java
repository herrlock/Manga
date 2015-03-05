package de.herrlock.manga.host;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import de.herrlock.manga.util.Utils;

class MangaPanda extends ChapterList {
    private static final long serialVersionUID = 1L;

    private final String name;

    public MangaPanda( URL url ) throws IOException {
        super();
        Document document = Utils.getDocument( url );

        this.name = document.select( "#mangaproperties h2.aname" ).first().text();

        Elements tr = document.select( "#chapterlist tr" );
        tr.remove( 0 );
        for ( Element e : tr ) {
            Element firstTd = e.getElementsByTag( "td" ).get( 0 );
            Element link = firstTd.getElementsByTag( "a" ).get( 0 );

            String[] nameAndNumber = link.text().split( " " );
            String number = nameAndNumber[nameAndNumber.length - 1];

            URL chapterUrl = new URL( url, link.attr( "href" ) );

            super.addChapter( number, chapterUrl );
        }
    }

    @Override
    public String getMangaName() {
        return this.name;
    }

    @Override
    public URL imgLink( URL url ) throws IOException {
        String src = Utils.getDocument( url ).getElementById( "img" ).attr( "src" );
        return new URL( url, src );
    }

    @Override
    public Map<Integer, URL> getAllPageURLs( URL url ) throws IOException {
        Map<Integer, URL> result = new HashMap<>();
        Elements pages = Utils.getDocument( url ).select( "#pageMenu>option" );
        for ( Element e : pages ) {
            result.put( Integer.parseInt( e.text() ), new URL( url, e.attr( "value" ) ) );
        }
        return Collections.unmodifiableMap( result );
    }

}
