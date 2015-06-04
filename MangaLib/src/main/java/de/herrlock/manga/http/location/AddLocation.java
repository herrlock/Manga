package de.herrlock.manga.http.location;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import de.herrlock.manga.http.ServerException;
import de.herrlock.manga.http.response.Response;

/**
 * @author Jan Rau
 *
 */
public class AddLocation extends Location {

    public AddLocation() {
        super( "add" );
    }

    @Override
    public Response handleXHR( URL url ) {
        String queryString = url.getQuery();
        if ( queryString == null ) {
            throw new ServerException( "queryString is null" );
        }
        String[] querys = queryString.split( "&" );

        Map<String, String> params = new HashMap<>( querys.length );
        for ( String param : querys ) {
            String[] paramArr = param.split( "=" );
            String value = "";
            if ( paramArr.length == 2 ) {
                value = paramArr[1];
            }
            params.put( paramArr[0], value );
        }

        System.out.println( params );
        // TODO: this function
        return null;
    }

}
