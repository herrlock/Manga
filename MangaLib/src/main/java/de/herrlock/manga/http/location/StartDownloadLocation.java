package de.herrlock.manga.http.location;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.Properties;

import de.herrlock.manga.downloader.MDownloader;
import de.herrlock.manga.exceptions.MyException;
import de.herrlock.manga.http.ServerException;
import de.herrlock.manga.http.response.Response;
import de.herrlock.manga.http.response.TextResponse;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * A Location to start the download
 * 
 * @author HerrLock
 */
public final class StartDownloadLocation extends Location {

    /**
     * Create a new StartDownloadLocation
     */
    public StartDownloadLocation() {
        super( "/start" );
    }

    @Override
    protected Response handleXHR( final URL url ) {
        String queryString = url.getQuery();
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
            } catch ( final UnsupportedEncodingException ex ) {
                throw new MyException( ex );
            }
            p.setProperty( paramArr[0], value );
        }

        final DownloadConfiguration conf = DownloadConfiguration.create( p );

        Thread t = new SimpleDownloaderThread( conf );
        t.start();

        Response res = new TextResponse( 301, "" );
        res.setHeader( "Location", "/" );
        return res;
    }

    private static final class SimpleDownloaderThread extends Thread {
        private final DownloadConfiguration conf;

        SimpleDownloaderThread( final DownloadConfiguration conf ) {
            this.conf = conf;
        }

        @Override
        public void run() {
            new SimpleDownloader( this.conf ).run();
        }
    }

    private static final class SimpleDownloader extends MDownloader {
        SimpleDownloader( final DownloadConfiguration conf ) {
            super( conf );
        }

        @Override
        protected void run() {
            downloadAll();
        }
    }

}
