package de.herrlock.manga.tomcat.servlet;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.UUID;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.annotations.VisibleForTesting;

import de.herrlock.manga.downloader.MDownloader;
import de.herrlock.manga.util.configuration.Configuration;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * @author HerrLock
 */
@Path( "download" )
public class DownloadServlet {
    private static final Logger logger = LogManager.getLogger();
    private static final Map<UUID, MDownloader> downloaders = new HashMap<>();

    @GET
    @Path( "start" )
    @Produces( MediaType.WILDCARD )
    public Response startDownload( @QueryParam( "url" ) final String url, @QueryParam( "proxy" ) final String proxy,
        @QueryParam( "pattern" ) final String pattern, @QueryParam( "folder" ) final String folder ) {
        logger.entry( url, proxy, pattern, folder );
        UUID randomUUID;
        do {
            randomUUID = UUID.randomUUID();
        } while ( downloaders.containsKey( randomUUID ) );

        final Properties p = new Properties();
        p.put( Configuration.HEADLESS, "true" );
        p.put( Configuration.URL, url );
        p.put( Configuration.PROXY, proxy );
        p.put( Configuration.PATTERN, pattern );
        final DownloadConfiguration conf = DownloadConfiguration.create( p );

        logger.info( "using Configuration: {}", conf );
        final PlainDownloader newDownloader = new PlainDownloader( conf );
        downloaders.put( randomUUID, newDownloader );

        logger.info( "starting downloader" );
        // newDownloader.run();
        logger.info( "downloader started" );

        return Response.ok( randomUUID.toString() ).build();
    }

    @GET
    @Path( "progress" )
    @Produces( MediaType.APPLICATION_JSON )
    public Response getProgress( @QueryParam( "uuid" ) final UUID uuid ) {
        logger.entry( uuid );
        MDownloader downloader = downloaders.get( uuid );
        // MDownloader downloader = this.downloaders.get( UUID.fromString( uuid ) );
        if ( downloader == null ) {
            return Response.serverError()//
                .entity( "No UUID found for \"" + uuid + "\"" )//
                .type( MediaType.TEXT_PLAIN_TYPE )//
                .build();
        }
        int progress = downloader.getProgress();
        int maxProgress = downloader.getMaxProgress();
        ProgressObject entity = new ProgressObject( progress, maxProgress );

        return Response.ok( entity ).build();
    }

    static class ProgressObject {
        private final int progress;
        private final int maxProgress;

        public ProgressObject( final int progress, final int maxProgress ) {
            this.progress = progress;
            this.maxProgress = maxProgress;
        }

        public int getProgress() {
            return this.progress;
        }

        public int getMaxProgress() {
            return this.maxProgress;
        }

    }

    @VisibleForTesting
    static final class PlainDownloader extends MDownloader {
        private final Thread thread;

        public PlainDownloader( final DownloadConfiguration conf ) {
            super( conf );
            this.thread = new Thread( new Runnable() {
                @Override
                public void run() {
                    PlainDownloader.this.downloadAll();
                }
            } );
        }

        @Override
        protected void run() {
            this.thread.start();
        }
    }
}
