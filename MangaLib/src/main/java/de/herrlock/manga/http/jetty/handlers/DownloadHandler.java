package de.herrlock.manga.http.jetty.handlers;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.UUID;

import javax.json.Json;
import javax.json.JsonArrayBuilder;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.net.MediaType;

import de.herrlock.manga.downloader.MDownloader;
import de.herrlock.manga.util.Utils;
import de.herrlock.manga.util.configuration.Configuration;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

public class DownloadHandler extends AbstractHandler {

    private static final Logger logger = LogManager.getLogger();

    public static final String PREFIX_PATH = "download";

    private final Handler downloadHandler = new StartDownloadHandler();
    private final Handler progressHandler = new GetProgressHandler();

    private final Map<UUID, MDObject> downloaders = new HashMap<>();

    @Override
    public void handle( final String target, final Request baseRequest, final HttpServletRequest request,
        final HttpServletResponse response ) throws IOException, ServletException {
        logger.info( target );
        if ( target != null ) {
            if ( target.startsWith( PREFIX_PATH + "/start" ) ) {
                logger.info( "download: {}", this.downloadHandler );
                // this.downloadHandler.handle( target, baseRequest, request, response );
            } else if ( target.startsWith( PREFIX_PATH + "/progress" ) ) {
                logger.info( "progress: {}", this.progressHandler );
                // this.progressHandler.handle( target, baseRequest, request, response );
            } else {
                // TODO
                logger.info( "other" );
            }
        }

    }

    /**
     * @author HerrLock
     */
    class StartDownloadHandler extends AbstractHandler {

        @Override
        public void handle( final String target, final Request baseRequest, final HttpServletRequest request,
            final HttpServletResponse response ) throws IOException, ServletException {
            String url = baseRequest.getParameter( "url" );
            String proxy = baseRequest.getParameter( "proxy" );
            String pattern = baseRequest.getParameter( "pattern" );

            logger.traceEntry( "URL: {}, Proxy: {}, Pattern: {}", url, proxy, pattern );
            UUID randomUUID;
            do {
                randomUUID = UUID.randomUUID();
            } while ( DownloadHandler.this.downloaders.containsKey( randomUUID ) );

            Properties p = Utils.newPropertiesBuilder() //
                .setProperty( Configuration.HEADLESS, "true" ) //
                .setProperty( Configuration.URL, url ) //
                .setProperty( Configuration.PROXY, proxy ) //
                .setProperty( Configuration.PATTERN, pattern ) //
                .build();
            final DownloadConfiguration conf = DownloadConfiguration.create( p );

            logger.info( "using Configuration: {}", conf );
            final PlainDownloader newDownloader = new PlainDownloader( conf );
            DownloadHandler.this.downloaders.put( randomUUID, new MDObject( url, newDownloader ) );

            logger.info( "starting downloader" );
            newDownloader.run();
            logger.info( "downloader started" );

            response.getWriter().write( randomUUID.toString() );
            response.setContentType( MediaType.PLAIN_TEXT_UTF_8.toString() );
            response.setStatus( HttpServletResponse.SC_OK );
            baseRequest.setHandled( true );
        }

    }

    /**
     * @author HerrLock
     */
    class GetProgressHandler extends AbstractHandler {

        @Override
        public void handle( final String target, final Request baseRequest, final HttpServletRequest request,
            final HttpServletResponse response ) throws IOException, ServletException {
            logger.traceEntry();
            JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
            for ( Entry<UUID, MDObject> entry : DownloadHandler.this.downloaders.entrySet() ) {
                UUID key = entry.getKey();
                MDObject value = entry.getValue();
                MDownloader mdownloader = value.getMdownloader();
                arrayBuilder.add( Json.createObjectBuilder() //
                    .add( "uuid", key.toString() ) //
                    .add( "url", value.getUrl() ) //
                    .add( "started", mdownloader.getStarted() ) //
                    .add( "progress", mdownloader.getProgress() ) //
                    .add( "maxProgress", mdownloader.getMaxProgress() ) );
            }
            Json.createWriter( response.getOutputStream() ).write( arrayBuilder.build() );
            response.setContentType( MediaType.JSON_UTF_8.toString() );
            response.setStatus( HttpServletResponse.SC_OK );
            baseRequest.setHandled( true );
        }

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
