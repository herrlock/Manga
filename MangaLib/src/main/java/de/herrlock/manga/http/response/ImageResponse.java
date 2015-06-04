package de.herrlock.manga.http.response;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

public class ImageResponse extends Response {
    private byte[] bytes;
    private String contentType = "image/jpg";

    public ImageResponse( InputStream imageStream ) throws IOException {
        super( 200 );
        if ( imageStream == null ) {
            setCode( 404 );
            this.bytes = "ImageNotFound".getBytes( UTF_8 );
        } else {
            final ByteArrayOutputStream out = new ByteArrayOutputStream();
            IOUtils.copy( imageStream, out );
            this.bytes = out.toByteArray();
        }
    }
    public ImageResponse( ByteArrayOutputStream out ) {
        super( 200 );
        this.bytes = out.toByteArray();
    }

    @Override
    public byte[] getData() {
        return Arrays.copyOf( this.bytes, this.bytes.length );
    }

    @Override
    protected String getContentType() {
        return this.contentType;
    }
}
