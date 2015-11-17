package de.herrlock.manga.http;

import de.herrlock.manga.http.location.BackgroundImageLocation;
import de.herrlock.manga.http.location.IndexHtmlLocation;
import de.herrlock.manga.http.location.JQueryLocation;
import de.herrlock.manga.http.location.NotFoundLocation;
import de.herrlock.manga.http.location.StartDownloadLocation;
import de.herrlock.manga.http.location.StopServerLocation;

public class DefaultServer extends Server {

    public static void main( String... args ) throws Exception {
        DefaultServer server = new DefaultServer( new IndexHtmlLocation(), new JQueryLocation(), new BackgroundImageLocation(),
            new StartDownloadLocation(), new StopServerLocation(), new NotFoundLocation() );
        server.start();
    }

    public DefaultServer( final HttpRequestHandlerWrapper... handler ) {
        super( handler );
    }

    public DefaultServer( final int port, final HttpRequestHandlerWrapper... handler ) {
        super( port, handler );
    }

}
