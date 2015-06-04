package de.herrlock.manga.http;

import java.io.ByteArrayOutputStream;

public class ImageResponse extends Response {
    private final ByteArrayOutputStream out;

    public ImageResponse( ByteArrayOutputStream out ) {
        super( 200 );
        this.out = out;
    }

    @Override
    public byte[] getData() {
        return this.out.toByteArray();
    }

    @Override
    protected String getContentType() {
        return "image/jpg";
    }
}
