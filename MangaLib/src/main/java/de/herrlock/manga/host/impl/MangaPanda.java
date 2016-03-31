package de.herrlock.manga.host.impl;

import java.io.IOException;
import java.net.URL;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.auto.service.AutoService;

import de.herrlock.manga.downloader.pmc.EntryList;
import de.herrlock.manga.host.ChapterList;
import de.herrlock.manga.host.Details;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

@AutoService( ChapterList.class )
@Details( name = "Mangapanda", baseUrl = "http://www.mangapanda.com/" )
public final class MangaPanda extends ChapterList {

    private final String name;

    public MangaPanda( final DownloadConfiguration conf ) throws IOException {
        super( conf );
        Document document = getDocument( conf.getUrl() );

        this.name = document.select( "#mangaproperties h2.aname" ).first().text();

        Elements tr = document.select( "#chapterlist tr" );
        tr.remove( 0 );
        for ( Element e : tr ) {
            Element firstTd = e.getElementsByTag( "td" ).first();
            Element link = firstTd.getElementsByTag( "a" ).first();

            String[] nameAndNumber = link.text().split( " " );
            String number = nameAndNumber[nameAndNumber.length - 1];

            URL chapterUrl = new URL( conf.getUrl(), link.attr( "href" ) );

            super.addChapter( number, chapterUrl );
        }
    }

    @Override
    public String getMangaName() {
        return this.name;
    }

    @Override
    public URL imgLink( final URL url ) throws IOException {
        String src = getDocument( url ).getElementById( "img" ).attr( "src" );
        return new URL( url, src );
    }

    @Override
    protected EntryList<Integer, URL> _getAllPageURLs( final URL url ) throws IOException {
        EntryList<Integer, URL> result = new EntryList<>();
        Elements pages = getDocument( url ).select( "#pageMenu>option" );
        for ( Element e : pages ) {
            Integer key = Integer.valueOf( e.text() );
            URL value = new URL( url, e.attr( "value" ) );
            result.addEntry( key, value );
        }
        return result;
    }

}
