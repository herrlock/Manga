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

import com.google.common.net.MediaType;

import de.herrlock.manga.downloader.DownloadProcessor;
import de.herrlock.manga.downloader.MDownloader;
import de.herrlock.manga.downloader.impl.PlainDownloader;
import de.herrlock.manga.util.Utils;
import de.herrlock.manga.util.configuration.Configuration;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

public class DownloadHandler extends AbstractHandler {
    private static final Logger logger = LogManager.getLogger();

    public static final String PREFIX_PATH = "download";

    private final DownloadHandlerContext dlContext = new DownloadHandlerContext();
    private final Handler downloadHandler = new StartDownloadHandler( this.dlContext );
    private final Handler progressHandler = new GetProgressHandler( this.dlContext );

    @Override
    public void handle( final String target, final Request baseRequest, final HttpServletRequest request,
        final HttpServletResponse response ) throws IOException, ServletException {
        logger.traceEntry();
        if ( target != null ) {
            if ( target.startsWith( PREFIX_PATH + "/start" ) ) {
                this.downloadHandler.handle( target, baseRequest, request, response );
            } else if ( target.startsWith( PREFIX_PATH + "/progress" ) ) {
                this.progressHandler.handle( target, baseRequest, request, response );
            }
        }
    }
}

final class DownloadHandlerContext {
    private final Map<UUID, MDObject> downloaders = new HashMap<>();

    public UUID put( final MDObject mdObject ) {
        UUID randomUUID;
                randomUUID = UUID.randomUUID();
        synchronized ( this.downloaders ) {
            this.downloaders.put( randomUUID, mdObject );
        }
        return randomUUID;
    }

    public Iterable<Entry<UUID, MDObject>> entrySet() {
        return this.downloaders.entrySet();
    }

}

/**
 * @author HerrLock
 */
final class StartDownloadHandler extends AbstractHandler {
    private static final Logger logger = LogManager.getLogger();

    private final DownloadHandlerContext dlContext;

    public StartDownloadHandler( final DownloadHandlerContext dlContext ) {
        this.dlContext = dlContext;
    }

    @Override
    public void handle( final String target, final Request baseRequest, final HttpServletRequest request,
        final HttpServletResponse response ) throws IOException, ServletException {
        logger.traceEntry();
        String url = baseRequest.getParameter( "url" );
        String proxy = baseRequest.getParameter( "proxy" );
        String pattern = baseRequest.getParameter( "pattern" );

        Properties p = Utils.newPropertiesBuilder() //
            .setProperty( Configuration.HEADLESS, "true" ) //
            .setProperty( Configuration.URL, url ) //
            .setProperty( Configuration.PROXY, proxy ) //
            .setProperty( Configuration.PATTERN, pattern ) //
            .build();
        final DownloadConfiguration conf = DownloadConfiguration.create( p );

        final MDownloader newDownloader = new PlainDownloader( conf );
        DownloadProcessor.getInstance().addDownload( newDownloader );

        UUID randomUUID = this.dlContext.put( new MDObject( url, newDownloader ) );
        response.getWriter().write( randomUUID.toString() );
        response.setContentType( MediaType.PLAIN_TEXT_UTF_8.toString() );
        response.setStatus( HttpServletResponse.SC_OK );
        baseRequest.setHandled( true );
    }

}

/**
 * @author HerrLock
 */
final class GetProgressHandler extends AbstractHandler {
    private static final Logger logger = LogManager.getLogger();

    private final DownloadHandlerContext dlContext;

    public GetProgressHandler( final DownloadHandlerContext dlContext ) {
        this.dlContext = dlContext;
    }

    @Override
    public void handle( final String target, final Request baseRequest, final HttpServletRequest request,
        final HttpServletResponse response ) throws IOException, ServletException {
        logger.traceEntry();
        JsonArrayBuilder arrayBuilder = Json.createArrayBuilder();
        for ( Entry<UUID, MDObject> entry : this.dlContext.entrySet() ) {
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
final class MDObject {
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
