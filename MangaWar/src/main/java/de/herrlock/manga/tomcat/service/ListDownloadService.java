package de.herrlock.manga.tomcat.service;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.DirectoryStream;
import java.nio.file.DirectoryStream.Filter;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ws.rs.GET;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.WebApplicationException;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;
import javax.ws.rs.core.StreamingOutput;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;

import de.herrlock.manga.util.Constants;

@javax.ws.rs.Path( "list" )
public final class ListDownloadService {
    private static final Logger logger = LogManager.getLogger();

    @GET
    @Produces( MediaType.APPLICATION_XML )
    public Response getEntries() {
        logger.info( "Reading downloads." );
        DownloadList entries = getNames();
        return Response.ok( entries ).build();
    }

    private DownloadList getNames() {
        logger.traceEntry();
        Path targetPath = Paths.get( Constants.TARGET_FOLDER );
        DownloadList entries = new DownloadList();
        if ( Files.exists( targetPath ) ) {
            try ( DirectoryStream<Path> downloads = Files.newDirectoryStream( targetPath, IS_DIR_FILTER ) ) {
                for ( Path path : downloads ) {
                    Entry entry = new Entry( path );
                    try ( DirectoryStream<Path> chapters = Files.newDirectoryStream( path, IS_DIR_FILTER ) ) {
                        entry.setChapterCount( Iterables.size( chapters ) );
                    }
                    entries.addDownload( entry );
                }
            } catch ( IOException e ) {
                logger.catching( e );
            }
        }
        return entries;
    }

    @GET
    @javax.ws.rs.Path( "{name}" )
    @Produces( MediaType.APPLICATION_OCTET_STREAM )
    public Response getSingleZip( @PathParam( "name" ) final String name ) {
        logger.info( "creating ZIP-file for {}", name );
        if ( name == null ) {
            throw new IllegalArgumentException( "Name must not be null" );
        }
        final Path downloadFolderPath = Paths.get( Constants.TARGET_FOLDER );
        final Path mangaFolder = downloadFolderPath.resolve( name );
        if ( !Files.exists( mangaFolder ) ) {
            throw new WebApplicationException( Status.NOT_FOUND );
        }
        ZipStreamingOutput streamingOutput = new ZipStreamingOutput( name, mangaFolder );
        return Response.ok( streamingOutput ) //
            .header( HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + name + ".zip" ) //
            .build();
    }

    /**
     * @author HerrLock
     */
    private static final class ZipStreamingOutput implements StreamingOutput {
        private final String mangaName;
        private final Path mangaFolder;

        private ZipStreamingOutput( final String mangaName, final Path mangaFolder ) {
            this.mangaName = Objects.requireNonNull( mangaName );
            this.mangaFolder = Objects.requireNonNull( mangaFolder );
        }

        @Override
        public void write( final OutputStream output ) throws IOException, WebApplicationException {
            try ( ZipAdder zipAdder = new ZipAdder( output, this.mangaName ) ) {
                Files.walkFileTree( this.mangaFolder, zipAdder );
            }
        }
    }

    /**
     * @author HerrLock
     */
    private static final class ZipAdder extends SimpleFileVisitor<Path> implements Closeable {
        private final ZipOutputStream zipOut;
        private final String mangaName;

        private ZipAdder( final OutputStream out, final String mangaName ) {
            this.zipOut = new ZipOutputStream( Objects.requireNonNull( out ) );
            this.mangaName = Objects.requireNonNull( mangaName );
        }

        @Override
        public FileVisitResult visitFile( final Path file, final BasicFileAttributes attrs ) throws IOException {
            String fileString = file.toString();
            int indexOfMangaName = fileString.indexOf( this.mangaName );
            String relativePath = fileString.substring( indexOfMangaName );
            ZipEntry zipEntry = new ZipEntry( relativePath );
            try ( InputStream in = Files.newInputStream( file ) ) {
                this.zipOut.putNextEntry( zipEntry );
                ByteStreams.copy( in, this.zipOut );
            } finally {
                this.zipOut.closeEntry();
            }
            return FileVisitResult.CONTINUE;
        }

        @Override
        public void close() throws IOException {
            this.zipOut.close();
        }

    }

    @XmlRootElement( name = "entries" )
    public static final class DownloadList {
        private final List<Entry> downloads = new ArrayList<>();

        public DownloadList() {
            // not used
        }

        public void addDownload( final Entry entry ) {
            this.downloads.add( entry );
        }

        @XmlElement( name = "entry" )
        public List<Entry> getDownloads() {
            return this.downloads;
        }
    }

    public static final class Entry {
        private final String name;
        private int chapterCount;

        public Entry( final Path path ) {
            Path fileName = path.getFileName();
            this.name = ( fileName == null ? "" : fileName ).toString();
        }

        public void setChapterCount( final int chapterCount ) {
            this.chapterCount = chapterCount;
        }

        @XmlElement
        public int getChapterCount() {
            return this.chapterCount;
        }

        @XmlElement
        public String getName() {
            return this.name;
        }
    }

    private static final Filter<Path> IS_DIR_FILTER = new Filter<Path>() {
        @Override
        public boolean accept( final Path entry ) throws IOException {
            return Files.isDirectory( entry );
        }
    };
}
