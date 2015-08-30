package de.herrlock.manga.http.location;

import java.net.URL;

import de.herrlock.manga.http.CloseServerException;
import de.herrlock.manga.http.response.Response;

public class StopServerLocation extends Location {

    public StopServerLocation() {
        super( "stopServer" );
    }

    @Override
    public Response handleXHR( URL url ) {
        throw new CloseServerException();
    }
}
