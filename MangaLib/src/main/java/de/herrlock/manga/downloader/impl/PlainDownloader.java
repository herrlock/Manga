package de.herrlock.manga.downloader.impl;

import de.herrlock.manga.downloader.MDownloader;
import de.herrlock.manga.util.configuration.DownloadConfiguration;

/**
 * A basic implementation of the {@link MDownloader} that simply calls {@link #downloadAll()}.
 * 
 * @author HerrLock
 */
public class PlainDownloader extends MDownloader {

    public PlainDownloader( final DownloadConfiguration conf ) {
        super( conf );
    }

    @Override
    protected void run() {
        downloadAll();
    }
}
