package de.herrlock.manga.http.jetty.handlers;

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

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXB;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Request;
import org.eclipse.jetty.server.handler.AbstractHandler;

import com.google.common.collect.Iterables;
import com.google.common.io.ByteStreams;
import com.google.common.net.HttpHeaders;
import com.google.common.net.MediaType;

import de.herrlock.manga.util.Constants;

/**
 * @author HerrLock
 */
public class GetListHandler extends AbstractHandler {
    private static final Logger logger = LogManager.getLogger();

    public static final String PREFIX_PATH = "list";

    private final Handler listHandler = new GetMangaListHandler();
    private final Handler zipHandler = new GetMangaZipHandler();

    @Override
    public void handle( final String target, final Request baseRequest, final HttpServletRequest request,
        final HttpServletResponse response ) throws IOException, ServletException {
        logger.info( target );
        if ( target != null && target.startsWith( PREFIX_PATH ) ) {
            if ( baseRequest.getParameter( "name" ) == null ) {
                this.listHandler.handle( target, baseRequest, request, response );
            } else {
                this.zipHandler.handle( target, baseRequest, request, response );
            }
        }

    }

    /**
     * @author HerrLock
     */
    static class GetMangaListHandler extends AbstractHandler {
        @Override
        public void handle( final String target, final Request baseRequest, final HttpServletRequest request,
            final HttpServletResponse response ) throws IOException, ServletException {
            DownloadList entries = getNames();
            JAXB.marshal( entries, response.getOutputStream() );
            response.setContentType( MediaType.XML_UTF_8.toString() );
            response.setStatus( HttpServletResponse.SC_OK );
            baseRequest.setHandled( true );
        }

        private DownloadList getNames() throws IOException {
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
                }
            }
            return entries;
        }
    }

    /**
     * @author HerrLock
     */
    static class GetMangaZipHandler extends AbstractHandler {
        @Override
        public void handle( final String target, final Request baseRequest, final HttpServletRequest request,
            final HttpServletResponse response ) throws IOException, ServletException {

            String name = baseRequest.getParameter( "name" );

            if ( name == null ) {
                throw new IllegalArgumentException( "Name must not be null" );
            }
            final Path downloadFolderPath = Paths.get( Constants.TARGET_FOLDER );
            final Path mangaFolder = downloadFolderPath.resolve( name );
            if ( !Files.exists( mangaFolder ) ) {
                throw new ServletException();
            }

            response.setHeader( HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + name + ".zip" );
            ZipStreamingOutput streamingOutput = new ZipStreamingOutput( name, mangaFolder );
            streamingOutput.write( response.getOutputStream() );

            baseRequest.setHandled( true );
        }
    }
    /**
     * @author HerrLock
     */
    public static final class ZipStreamingOutput {
        private final String mangaName;
        private final Path mangaFolder;

        public ZipStreamingOutput( final String mangaName, final Path mangaFolder ) {
            this.mangaName = Objects.requireNonNull( mangaName );
            this.mangaFolder = Objects.requireNonNull( mangaFolder );
        }

        public void write( final OutputStream output ) throws IOException {
            try ( ZipAdder zipAdder = new ZipAdder( output, this.mangaName ) ) {
                Files.walkFileTree( this.mangaFolder, zipAdder );
            }
        }
    }

    /**
     * @author HerrLock
     */
    public static final class ZipAdder extends SimpleFileVisitor<Path> implements Closeable {
        private final ZipOutputStream zipOut;
        private final String mangaName;

        public ZipAdder( final OutputStream out, final String mangaName ) {
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
