package de.herrlock.manga.downloader;

import java.io.OutputStream;
import java.util.Properties;

public class UIDownloader extends MDownloader {

    public static void execute() {
        throw new UnsupportedOperationException( "UIDownloader.execute()" );
    }

    public UIDownloader( Properties p, OutputStream out ) {
        super( p, out );
        throw new UnsupportedOperationException();
    }

    @Override
    public void run() {
        // TODO Auto-generated method stub

    }

}
