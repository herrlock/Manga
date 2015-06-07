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

final class MangaFox extends ChapterList {
    private static final long serialVersionUID = 1L;

    private final String name;

    public MangaFox( URL url, DownloadConfiguration conf ) throws IOException {
        super( conf );
        Document document = Utils.getDocument( url, conf );

        this.name = document.select( "#series_info>.cover>img" ).first().attr( "alt" );

        Elements elements = document.select( "#chapters>ul.chlist>li" );
        for ( Element e : elements ) {
            Element h3 = e.select( "h3" ).first();
            if ( h3 == null ) {
                h3 = e.select( "h4" ).first();
            }
            Element a = h3.select( "a.tips" ).first();

            String[] nnumber = a.text().split( " " );
            String number = nnumber[nnumber.length - 1];

            URL chapterUrl = new URL( url, a.attr( "href" ) );

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
        String src = Utils.getDocument( url, this.conf ).getElementById( "image" ).attr( "src" );
        return new URL( url, src );
    }

    @Override
    protected Map<Integer, URL> _getAllPageURLs( URL url ) throws IOException {
        Map<Integer, URL> result = new ConcurrentHashMap<>();
        Elements pages = Utils.getDocument( url, this.conf ).select( "select.m" ).first().getElementsByTag( "option" );
        Element last = pages.last();
        if ( "Comments".equals( last.text() ) ) {
            pages.remove( last );
        }
        for ( Element e : pages ) {
            int number = Integer.parseInt( e.text() );
            result.put( number, new URL( url, e.attr( "value" ) + ".html" ) );
        }
        return result;
    }

}
