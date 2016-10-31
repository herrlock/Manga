package de.herrlock.manga.http.jetty.handlers;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.HandlerList;

public class MangaBaseHandler extends HandlerList {
    private static final Logger logger = LogManager.getLogger();

    public static final String PREFIX_PATH = "/j/";

    private final Handler imageHandler = new ReturnImageHandler();
    private final Handler downloadHandler = new DownloadHandler();
    private final Handler listHandler = new GetListHandler();

    @Override
    public void handle( final String target, final Request baseRequest, final HttpServletRequest request,
        final HttpServletResponse response ) throws IOException, ServletException {
        logger.entry( target );

        if ( target != null && target.startsWith( PREFIX_PATH ) ) {
            String subPath = target.substring( PREFIX_PATH.length() );
            if ( subPath.equals( ReturnImageHandler.PREFIX_PATH ) ) {
                this.imageHandler.handle( subPath, baseRequest, request, response );
            } else if ( subPath.startsWith( DownloadHandler.PREFIX_PATH ) ) {
                this.downloadHandler.handle( subPath, baseRequest, request, response );
            } else if ( subPath.startsWith( GetListHandler.PREFIX_PATH ) ) {
                this.listHandler.handle( subPath, baseRequest, request, response );
            }
        }
    }

}
