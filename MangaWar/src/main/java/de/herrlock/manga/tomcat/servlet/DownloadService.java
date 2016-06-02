package de.herrlock.manga.tomcat.servlet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
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
 * A service that receives the parameters required to start an {@link MDownloader} and handles the currently running MDownloaders
 * 
 * @author HerrLock
 */
@Path( "download" )
public class DownloadService {
    private static final Logger logger = LogManager.getLogger();
    private static final Map<UUID, MDObject> downloaders = new HashMap<>();

    /**
     * Creates a new {@link UUID} and a new {@link DownloadConfiguration} and creates and start a new {@link MDownloader}
     * afterwards.
     * 
     * @param url
     *            the url to download from
     * @param proxy
     *            the url to the proxy to use
     * @param pattern
     *            the chapter-pattern
     * @return a response containing the UUID for the new MDownloader
     */
    @GET
    @Path( "start" )
    @Produces( MediaType.TEXT_PLAIN )
    public Response startDownload( @QueryParam( "url" ) final String url, @QueryParam( "proxy" ) final String proxy,
        @QueryParam( "pattern" ) final String pattern ) {
        logger.entry( url, proxy, pattern );
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
        downloaders.put( randomUUID, new MDObject( url, newDownloader ) );

        logger.info( "starting downloader" );
        newDownloader.run();
        logger.info( "downloader started" );

        return Response.ok( randomUUID.toString() ).build();
    }

    /**
     * Retrieves and returns the progress of all MDownloaders.
     * 
     * @return a list of progresses for all MDownloaders
     */
    @GET
    @Path( "progress" )
    @Produces( MediaType.APPLICATION_JSON )
    public List<ProgressObject> getProgress() {
        logger.entry();
        List<ProgressObject> result = new ArrayList<>( downloaders.size() );
        for ( Entry<UUID, MDObject> entry : downloaders.entrySet() ) {
            UUID key = entry.getKey();
            MDObject value = entry.getValue();
            MDownloader mdownloader = value.getMdownloader();
            ProgressObject entity = new ProgressObject( key, value.getUrl(), mdownloader.getStarted(), mdownloader.getProgress(),
                mdownloader.getMaxProgress() );
            result.add( entity );
        }
        return result;
    }

    /**
     * A simple object to store MDownloaders together with the url
     * 
     * @author HerrLock
     */
    static class MDObject {
        private final String url;
        private final MDownloader mdownloader;

        public MDObject( final String url, final MDownloader mdownloader ) {
            this.url = url;
            this.mdownloader = mdownloader;
        }

        public String getUrl() {
            return this.url;
        }

        public MDownloader getMdownloader() {
            return this.mdownloader;
        }
    }

    /**
     * An object to store properties that are related to the progress of an MDownloader.
     * 
     * @author HerrLock
     */
    static class ProgressObject {
        private final UUID uuid;
        private final String url;
        private final boolean started;
        private final int progress;
        private final int maxProgress;

        public ProgressObject( final UUID uuid, final String url, final boolean started, final int progress,
            final int maxProgress ) {
            this.uuid = uuid;
            this.url = url;
            this.started = started;
            this.progress = progress;
            this.maxProgress = maxProgress;
        }

        public UUID getUuid() {
            return this.uuid;
        }

        public String getUrl() {
            return this.url;
        }

        public boolean getStarted() {
            return this.started;
        }

        public int getProgress() {
            return this.progress;
        }

        public int getMaxProgress() {
            return this.maxProgress;
        }

    }

    /**
     * An {@link MDownloader} that starts the download in a new Thread.
     * 
     * @author HerrLock
     */
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
