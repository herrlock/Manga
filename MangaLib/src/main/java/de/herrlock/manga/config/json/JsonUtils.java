package de.herrlock.manga.config.json;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import com.google.gson.Gson;

import de.herrlock.manga.exceptions.MDRuntimeException;

/**
 * @author HerrLock
 */
public final class JsonUtils {

    // private static final Logger logger = LogManager.getLogger();

    private static final Gson GSON = new Gson();

    public static JsonConfiguration readJson( final InputStream source ) {
        JsonConfiguration jsonConfiguration;
        try ( Reader jsonReader = new InputStreamReader( source, StandardCharsets.UTF_8 ) ) {
            jsonConfiguration = GSON.fromJson( jsonReader, JsonConfiguration.class );
        } catch ( IOException ex ) {
            throw new MDRuntimeException( ex );
        }
        return jsonConfiguration;
    }

    private JsonUtils() {
        // suppress instantiation
    }

}
