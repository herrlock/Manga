package de.herrlock.manga.http.location;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

import de.herrlock.manga.downloader.MDownloader;
import de.herrlock.manga.http.ServerException;
import de.herrlock.manga.http.response.Response;
import de.herrlock.manga.http.response.TextResponse;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * @author HerrLock
 */
public class StartDownloadLocation extends Location {

    public StartDownloadLocation() {
        super( "start" );
    }

    @Override
    public Response handleXHR( URL requestUrl ) {
        String queryString = requestUrl.getQuery();
        if ( queryString == null ) {
            throw new ServerException( "queryString is null" );
        }
        String[] querys = queryString.split( "&" );
        final Properties p = new Properties();
        for ( String param : querys ) {
            String[] paramArr = param.split( "=" );
            String value = "";
            if ( paramArr.length == 2 ) {
                value = paramArr[1];
            }
            try {
                value = URLDecoder.decode( value, "UTF-8" );
            } catch ( UnsupportedEncodingException ex ) {
                throw new RuntimeException( ex );
            }
            p.setProperty( paramArr[0], value );
        }

        final DownloadConfiguration conf = DownloadConfiguration.create( p );

        Thread t = new SimpleDownloaderThread( conf );
        t.start();

        TextResponse res = new TextResponse( 301 );
        res.setHeader( "Location", "/md" );
        return res;
    }

    private static final class SimpleDownloaderThread extends Thread {
        private final DownloadConfiguration conf;

        SimpleDownloaderThread( DownloadConfiguration conf ) {
            this.conf = conf;
        }

        @Override
        public void run() {
            new SimpleDownloader( this.conf ).run();
        }
    }

    private static final class SimpleDownloader extends MDownloader {
        SimpleDownloader( DownloadConfiguration conf ) {
            super( conf );
        }

        @Override
        protected void run() {
            downloadAll();
        }
    }

}
