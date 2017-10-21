package de.herrlock.manga.viewpage;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.DirectoryStream;
import java.nio.file.FileVisitResult;
import java.nio.file.FileVisitor;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.annotations.VisibleForTesting;

import de.herrlock.manga.exceptions.MDRuntimeException;

/**
 * @author HerrLock
 */
public final class ViewArchive {
    private static final Logger logger = LogManager.getLogger();

    private final Path targetFolder;
    private final File folder;
    private final Format format;
    private final boolean clean;

    /**
     * Creates the new ViewArchive-instance and writes the comicbook-archive for the given folder
     * 
     * @param folder
     *            the folder to read the chapters from
     * @param format
     *            the format of the archive to create, see {@link Format}
     * @param clean
     *            remove the downloaded images after archiving them
     * 
     */
    public static void execute( final File folder, final String format, final boolean clean ) {
        logger.entry( folder, format, clean );
        ViewArchive viewPage = new ViewArchive( folder, format, clean );
        viewPage.write();
    }

    /**
     * Create a new ViewArchive for the given {@linkplain File folder}
     * 
     * @param folder
     *            the folder containing the downloaded chapters
     * @param format
     *            the format of the archive to create, see {@link Format}
     * @param clean
     *            remove the downloaded images after archiving them
     */
    private ViewArchive( final File folder, final String format, final boolean clean ) {
        logger.entry( folder, format, clean );
        this.folder = folder;
        try {
            this.targetFolder = Files.createDirectories( Paths.get( "comicbookarchives", mangaName() ) );
        } catch ( IOException ex ) {
            throw new MDRuntimeException( ex );
        }
        this.format = Format.valueOf( format.toUpperCase( Locale.GERMAN ) );
        this.clean = clean;
    }

    private String mangaName() {
        String foldername = this.folder.getName();
        String formattedManganame = ViewPageConstants.formatManganame( foldername );
        return formattedManganame.replace( ' ', '-' );
    }

    private String decimalTransform( final String base, final int length ) {
        BigDecimal decimal = new BigDecimal( base );
        BigInteger beforeComma = decimal.toBigInteger();
        BigDecimal afterComma = decimal.remainder( BigDecimal.ONE );
        String beforeCommaString = beforeComma.toString();
        String afterCommaString = afterComma.toPlainString();
        return padWith0( beforeCommaString, length ) + afterCommaString.substring( 1 );
    }

    @VisibleForTesting
    static String toArchiveEntryFilename( final String filename ) {
        if ( filename == null ) {
            return "";
        }
        if ( filename.matches( "\\d+\\.jpg" ) ) {
            return padWith0( filename, 7 );
        }
        return filename;
    }

    @VisibleForTesting
    static String padWith0( final String base, final int length ) {
        StringBuilder sb = new StringBuilder( base );
        while ( sb.length() < length ) {
            sb.insert( 0, '0' );
        }
        return sb.toString();
    }

    private void write() {
        try {
            if ( this.format == Format.CBZ ) {
                writeCBZ();
            } else if ( this.format == Format.CBT ) {
                writeCBT();
            } else {
                logger.warn( "Unsupported format given: {}", this.format );
            }
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }

    private void writeCBZ() throws IOException {
        Path folderPath = this.folder.toPath();
        try ( DirectoryStream<Path> directoryStream = Files.newDirectoryStream( folderPath,
            new ViewPageConstants.PathIsDirectoryFilter() ) ) {
            for ( Path path : directoryStream ) {
                String chapterName = decimalTransform( String.valueOf( path.getFileName() ), 4 );
                Path zipArchive = this.targetFolder.resolve( mangaName() + '-' + chapterName + ".cbz" );
                try ( ZipOutputStream archiveZip = new ZipOutputStream( Files.newOutputStream( zipArchive ) ) ) {
                    writeZipEntry( archiveZip, path );
                }
                if ( this.clean ) {
                    Files.walkFileTree( path, DELETE_VISITOR );
                }
            }
        }
        if ( this.clean ) {
            Files.walkFileTree( folderPath, DELETE_VISITOR );
        }
    }

    private void writeZipEntry( final ZipOutputStream archiveZip, final Path current ) throws IOException {
        try ( DirectoryStream<Path> dirStream = Files.newDirectoryStream( current ) ) {
            for ( Path image : dirStream ) {
                String imageName = toArchiveEntryFilename( String.valueOf( image.getFileName() ) );
                try {
                    ZipEntry imageEntry = new ZipEntry( imageName );
                    archiveZip.putNextEntry( imageEntry );
                    Files.copy( image, archiveZip );
                } finally {
                    archiveZip.closeEntry();
                }
            }
        }
    }

    private void writeCBT() throws IOException {
        Path folderPath = this.folder.toPath();
        try ( DirectoryStream<Path> directoryStream = Files.newDirectoryStream( folderPath,
            new ViewPageConstants.PathIsDirectoryFilter() ) ) {
            for ( Path path : directoryStream ) {
                String chapterName = decimalTransform( String.valueOf( path.getFileName() ), 4 );
                Path tarArchive = this.targetFolder.resolve( mangaName() + '-' + chapterName + ".cbt" );
                try ( TarArchiveOutputStream archiveZip = new TarArchiveOutputStream( Files.newOutputStream( tarArchive ) ) ) {
                    writeTarEntry( archiveZip, path );
                }
                if ( this.clean ) {
                    Files.walkFileTree( path, DELETE_VISITOR );
                }
            }
        }
        if ( this.clean ) {
            Files.walkFileTree( folderPath, DELETE_VISITOR );
        }
    }

    private void writeTarEntry( final TarArchiveOutputStream cbtStream, final Path current ) throws IOException {
        try ( DirectoryStream<Path> dirStream = Files.newDirectoryStream( current ) ) {
            for ( Path image : dirStream ) {
                String imageName = toArchiveEntryFilename( String.valueOf( image.getFileName() ) );
                try {
                    TarArchiveEntry tarEntry = new TarArchiveEntry( image.toFile(), imageName );
                    cbtStream.putArchiveEntry( tarEntry );
                    Files.copy( image, cbtStream );
                } finally {
                    cbtStream.closeArchiveEntry();
                }
            }
        }
    }

    public enum Format {
        CBZ, CBT;
    }

    private static final FileVisitor<? super Path> DELETE_VISITOR = new SimpleFileVisitor<Path>() {

        @Override
        public FileVisitResult visitFile( final Path file, final BasicFileAttributes attrs ) throws IOException {
            Files.delete( file );
            return FileVisitResult.CONTINUE;
        }

        @Override
        public FileVisitResult postVisitDirectory( final Path dir, final IOException exc ) throws IOException {
            if ( exc != null ) {
                throw exc;
            }
            Files.delete( dir );
            return FileVisitResult.CONTINUE;
        }

    };

}
