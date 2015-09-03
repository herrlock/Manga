package de.herrlock.manga.http.response;

import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;

import org.apache.commons.io.IOUtils;

/**
 * A {@link Response} that returns an image. The stored data cannot be changed after creation.
 * 
 * @author HerrLock
 */
public class ImageResponse extends Response {
    private final byte[] bytes;

    /**
     * Creates a new ImageResponse with the bytes loaded from the given InputStream
     * 
     * @param imageStream
     *            the {@link InputStream} to load the image from
     * @throws IOException
     *             thrown by {@link IOUtils#copy(InputStream, java.io.OutputStream)}
     */
    public ImageResponse( InputStream imageStream ) throws IOException {
        super( 200 );
        try {
            if ( imageStream == null ) {
                setCode( 404 );
                this.bytes = "ImageNotFound".getBytes( UTF_8 );
            } else {
                final ByteArrayOutputStream out = new ByteArrayOutputStream();
                IOUtils.copy( imageStream, out );
                this.bytes = out.toByteArray();
            }
        } finally {
            IOUtils.closeQuietly( imageStream );
        }
    }

    /**
     * Creates a new ImageResponse with the bytes stores in the given {@link ByteArrayOutputStream}
     * 
     * @param out
     *            a {@link ByteArrayOutputStream} that contains the image-data
     */
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
        return "image";
    }
}
