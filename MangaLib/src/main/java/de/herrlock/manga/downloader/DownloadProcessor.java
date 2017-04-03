package de.herrlock.manga.downloader;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author HerrLock
 */
public class DownloadProcessor {

    private static DownloadProcessor _INSTANCE = new DownloadProcessor();
    private DownloadExecutor executor;
    private final Object executorMon = new Object();

    private final BlockingQueue<MDownloader> queue = new LinkedBlockingQueue<>();

    public static DownloadProcessor getInstance() {
        return _INSTANCE;
    }

    public void addDownload( final MDownloader mdownloader ) {
        this.queue.add( mdownloader );
        synchronized ( this.executorMon ) {
            if ( this.executor == null ) {
                this.executor = new DownloadExecutor();
                this.executor.start();
            }
        }
    }

    public class DownloadExecutor extends Thread {

        public DownloadExecutor() {
            setName( "DownloadExecutor" );
        }

        @Override
        public void run() {
            try {
                process();
            } catch ( InterruptedException ex ) {
                // logger.error("DownloadExecutor interrupted", ex);
            }
        }

        private void process() throws InterruptedException {
            boolean exit = false;
            do {
                MDownloader mdownloader = DownloadProcessor.this.queue.take();
                mdownloader.run();
                synchronized ( DownloadProcessor.this.executorMon ) {
                    if ( DownloadProcessor.this.queue.isEmpty() ) {
                        DownloadProcessor.this.executor = null;
                        exit = true;
                    }
                }
            } while ( !exit );

        }
    }

}
