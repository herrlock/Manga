package de.herrlock.manga.host.impl;

import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.Collections;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.auto.service.AutoService;

import de.herrlock.manga.downloader.pmc.EntryList;
import de.herrlock.manga.host.ChapterList;
import de.herrlock.manga.host.HosterImpl;
import de.herrlock.manga.host.annotations.ChapterListDetails;
import de.herrlock.manga.host.annotations.Details;
import de.herrlock.manga.index.HosterListEntry;
import de.herrlock.manga.util.configuration.DownloadConfiguration;
import de.herrlock.manga.util.configuration.IndexerConfiguration;

@AutoService( HosterImpl.class )
@Details( name = "Manga-Tube", baseUrl = "http://www.manga-tube.com/" )
public final class MangaTube extends HosterImpl {

    @Override
    public ChapterList getChapterList( final DownloadConfiguration conf ) throws IOException {
        return new MangaTubeChapterList( conf );
    }

    @Override
    public Collection<HosterListEntry> getAvailabile( final IndexerConfiguration conf ) {
        // TODO
        return Collections.emptyList();
    }

}

@ChapterListDetails( reversed = true )
final class MangaTubeChapterList extends ChapterList {
    private final String name;

    public MangaTubeChapterList( final DownloadConfiguration conf ) throws IOException {
        super( conf );
        Document document = getDocument( conf.getUrl() );

        this.name = document.select( "h1.title" ).first().text();

        Elements elements = document.select( "div.group > div.element" );
        for ( Element e : elements ) {
            Element titleDiv = e.select( "div.title" ).first();
            Element link = titleDiv.getElementsByTag( "a" ).first();

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
        String src = getDocument( url ).select( "#page img" ).first().attr( "src" );
        return new URL( url, src );
    }

    @Override
    protected EntryList<Integer, URL> _getAllPageURLs( final URL url ) throws IOException {
        EntryList<Integer, URL> result = new EntryList<>();
        Elements pages = getDocument( url ).select( "div.topbar_right > div > ul.dropdown > li > a" );
        for ( Element e : pages ) {
            Integer key = Integer.valueOf( e.text().split( " " )[1] );
            URL value = new URL( url, e.attr( "href" ) );
            result.addEntry( key, value );
        }
        return result;
    }

}
