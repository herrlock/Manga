package de.herrlock.manga.host.impl;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collection;
import java.util.TreeSet;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
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
import de.herrlock.manga.http.client.JettyClient;
import de.herrlock.manga.index.entity.HosterListEntry;
import de.herrlock.manga.util.configuration.DownloadConfiguration;
import de.herrlock.manga.util.configuration.IndexerConfiguration;

@AutoService( HosterImpl.class )
@Details( name = "Mangafox", baseUrl = "http://www.mangafox.me/" )
public class MangaFox extends HosterImpl {
    private static final Logger logger = LogManager.getLogger();

    @Override
    public ChapterList getChapterList( final DownloadConfiguration conf ) throws IOException {
        return new MangaFoxChapterList( conf );
    }

    @Override
    public Collection<HosterListEntry> getAvailabile( final IndexerConfiguration conf ) {
        Document doc;
        final URL baseUrl;
        try {
            baseUrl = new URL( getDetails().baseUrl() );
            URL listUrl = new URL( baseUrl, "/manga/" );
            doc = JettyClient.getDocument( listUrl, conf );
        } catch ( IOException ex ) {
            throw new MDRuntimeException( ex );
        }
        Elements elements = doc.select( "div#page div.manga_list > ul > li > a" );
        Collection<HosterListEntry> entries = new TreeSet<>( HosterListEntry.NAME_COMPARATOR );
        for ( Element element : elements ) {
            HosterListEntry entry = new HosterListEntry();
            entry.setName( element.text() );
            try {
                entry.setUrl( new URL( baseUrl, element.attr( "href" ) ).toExternalForm() );
            } catch ( MalformedURLException ex ) {
                logger.catching( Level.DEBUG, ex );
            }
            entries.add( entry );
        }
        return entries;
    }

}

@ChapterListDetails( reversed = true )
final class MangaFoxChapterList extends ChapterList {
    private final String name;

    public MangaFoxChapterList( final DownloadConfiguration conf ) throws IOException {
        super( conf );
        Document document = getDocument( conf.getUrl() );

        this.name = document.select( "#series_info > .cover > img" ).first().attr( "alt" );

        Elements elements = document.select( "#chapters > ul.chlist > li" );
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
