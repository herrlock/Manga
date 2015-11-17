package de.herrlock.manga.http.location;

import java.net.URL;

import de.herrlock.manga.http.CloseServerException;
import de.herrlock.manga.http.response.Response;

/**
 * A Location to request the Server to stop
 * 
 * @author Herrlock
 */
public final class StopServerLocation extends Location {

    /**
     * Create a new StopServerLocation
     */
    public StopServerLocation() {
        super( "/stopServer" );
    }

    @Override
    protected Response handleXHR( final URL url ) {
        throw new CloseServerException();
    }
}
