package de.herrlock.manga.host.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Collections;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.auto.service.AutoService;

import de.herrlock.manga.downloader.pmc.EntryList;
import de.herrlock.manga.host.ChapterList;
import de.herrlock.manga.host.Details;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

@AutoService( ChapterList.class )
@Details( name = "Mangafox", baseUrl = "http://www.mangafox.me/", reversed = true )
public final class MangaFox extends ChapterList {
    private static final long serialVersionUID = 1L;

    private final String name;

    public MangaFox( final DownloadConfiguration conf ) throws IOException {
        super( conf );
        Document document = getDocument( conf.getUrl() );

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

            URL chapterUrl = new URL( conf.getUrl(), a.attr( "href" ) );

            super.addChapter( number, chapterUrl );
        }
        Collections.reverse( this );
    }

    @Override
    public String getMangaName() {
        return this.name;
    }

    @Override
    public URL imgLink( final URL url ) throws IOException {
        String src = getDocument( url ).getElementById( "image" ).attr( "src" );
        return new URL( url, src );
    }

    @Override
    protected EntryList<Integer, URL> _getAllPageURLs( final URL url ) throws IOException {
        EntryList<Integer, URL> result = new EntryList<>();
        Elements pages = getDocument( url ).select( "select.m" ).first().getElementsByTag( "option" );
        Element last = pages.last();
        if ( "Comments".equals( last.text() ) ) {
            pages.remove( last );
        }
        for ( Element e : pages ) {
            int number = Integer.parseInt( e.text() );
            URL value = new URL( url, e.attr( "value" ) + ".html" );
            result.addEntry( number, value );
        }
        return result;
    }

}
