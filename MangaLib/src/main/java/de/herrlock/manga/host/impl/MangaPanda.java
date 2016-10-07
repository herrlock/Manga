package de.herrlock.manga.host.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.TreeSet;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.google.auto.service.AutoService;

import de.herrlock.manga.downloader.pmc.EntryList;
import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.host.ChapterList;
import de.herrlock.manga.host.HosterImpl;
import de.herrlock.manga.host.annotations.ChapterListDetails;
import de.herrlock.manga.host.annotations.Details;
import de.herrlock.manga.index.HosterListEntry;
import de.herrlock.manga.util.Utils;
import de.herrlock.manga.util.configuration.DownloadConfiguration;
import de.herrlock.manga.util.configuration.IndexerConfiguration;

@AutoService( HosterImpl.class )
@Details( name = "Mangapanda", baseUrl = "http://www.mangapanda.com/" )
public final class MangaPanda extends HosterImpl {

    @Override
    public ChapterList getChapterList( final DownloadConfiguration conf ) throws IOException {
        return new MangaPandaChapterList( conf );
    }

    @Override
    public Collection<HosterListEntry> getAvailabile( final IndexerConfiguration conf ) {
        Document doc;
        final URL baseUrl;
        try {
            baseUrl = new URL( getDetails().baseUrl() );
            URL listUrl = new URL( baseUrl, "/alphabetical" );
            doc = Utils.getDocument( listUrl, conf );
        } catch ( IOException ex ) {
            throw new MDRuntimeException( ex );
        }
        Elements elements = doc.select( "div.series_col ul.series_alpha > li > a" );
        Collection<HosterListEntry> entries = new TreeSet<>( HosterListEntry.NAME_COMPARATOR );
        for ( Element element : elements ) {
            HosterListEntry entry = new HosterListEntry();
            entry.setName( element.text() );
            try {
                entry.setUrl( new URL( baseUrl, element.attr( "href" ) ) );
            } catch ( MalformedURLException ex ) {
                // ignore
            }
            entries.add( entry );
        }
        return entries;
    }

}

@ChapterListDetails
final class MangaPandaChapterList extends ChapterList {

    private final String name;

    public MangaPandaChapterList( final DownloadConfiguration conf ) throws IOException {
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
        Elements pages = getDocument( url ).select( "#pageMenu > option" );
        for ( Element e : pages ) {
            Integer key = Integer.valueOf( e.text() );
            URL value = new URL( url, e.attr( "value" ) );
            result.addEntry( key, value );
        }
        return result;
    }

}
