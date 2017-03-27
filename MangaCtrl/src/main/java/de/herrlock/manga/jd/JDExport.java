package de.herrlock.manga.jd;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.concurrent.Callable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.ByteStreams;

import de.herrlock.manga.downloader.DownloadProcessor;
import de.herrlock.manga.downloader.MDownloader;
import de.herrlock.manga.downloader.pmc.EntryList;
import de.herrlock.manga.exceptions.InitializeException;
import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.Utils;
import de.herrlock.manga.util.configuration.Configuration;
import de.herrlock.manga.util.configuration.JDConfiguration;

public final class JDExport extends MDownloader {
    private static final Logger logger = LogManager.getLogger();

    final File jdfwFolder;
    final File path = this.clc.getPath();

    public static void executeGetFileProperties() {
        Properties p = new Properties();
        try {
            try ( InputStream fIn = new FileInputStream( Constants.SETTINGS_FILE ) ) {
                p.load( fIn );
            }
        } catch ( IOException ex ) {
            throw new MDRuntimeException( ex );
        }
        execute( p );
    }

    public static void execute( final Properties p ) {
        logger.traceEntry();
        String jdhome = p.getProperty( Configuration.JDFW );
        if ( jdhome == null || jdhome.trim().isEmpty() ) {
            throw new InitializeException( "\"" + Configuration.JDFW + "\" must be set" );
        }
        JDConfiguration conf = JDConfiguration.create( p );
        MDownloader dlImpl = new JDExport( conf );
        DownloadProcessor.getInstance().addDownload( dlImpl );
    }

    public JDExport( final JDConfiguration conf ) {
        super( conf );
        this.jdfwFolder = conf.getFolderwatch();
        if ( !( this.jdfwFolder.exists() || this.jdfwFolder.mkdir() ) ) {
            throw new MDRuntimeException( this.jdfwFolder + " does not exist and could not be created" );
        }
    }

    public URL getImageLink( final URL pageUrl ) throws IOException {
        return this.clc.getImageLink( pageUrl );
    }

    @Override
    protected void run() {
        try {
            EntryList<String, EntryList<Integer, URL>> pmcEntries = this.pmc.getEntries();
            for ( Entry<String, EntryList<Integer, URL>> entry : pmcEntries ) {
                new CrawljobFile( entry.getKey(), entry.getValue() ).write();
            }
        } catch ( IOException ex ) {
            throw new MDRuntimeException( ex );
        }
    }

    private final class CrawljobFile {
        final Crawljob c;

        public CrawljobFile( final String chapter, final EntryList<Integer, URL> entrySet ) {
            this.c = new Crawljob( new File( JDExport.this.path, chapter ), chapter );
            List<CrawljobFileEntryAdder> callables = new ArrayList<>( entrySet.size() );
            for ( Entry<Integer, URL> e : entrySet ) {
                callables.add( new CrawljobFileEntryAdder( e ) );
            }
            Utils.callCallables( callables );
        }

        public void write() throws IOException {
            File outFile = new File( JDExport.this.jdfwFolder, this.c.getFilename() );
            byte[] bytes = this.c.export().getBytes( StandardCharsets.UTF_8 );
            try ( OutputStream out = new FileOutputStream( outFile ) ) {
                ByteStreams.copy( new ByteArrayInputStream( bytes ), out );
            }
            logger.info( "print string -> {}", outFile );
        }

        private final class CrawljobFileEntryAdder implements Callable<Void> {
            private final Entry<Integer, URL> e;

            public CrawljobFileEntryAdder( final Entry<Integer, URL> e ) {
                this.e = e;
            }

            @Override
            public Void call() {
                String filename = this.e.getKey().toString();
                String pictureURL = getImageLink().toExternalForm();
                CrawljobFile.this.c.addCrawljobEntry( filename, pictureURL );
                return null;
            }

            private URL getImageLink() {
                try {
                    return JDExport.this.getImageLink( this.e.getValue() );
                } catch ( SocketTimeoutException ex ) {
                    return getImageLink();
                } catch ( IOException ex ) {
                    throw new MDRuntimeException( ex );
                }
            }
        }
    }
}
