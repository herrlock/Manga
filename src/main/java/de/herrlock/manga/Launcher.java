package de.herrlock.manga;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Unpacker;
import java.util.zip.GZIPInputStream;

/**
 * Entrance class for the jar, first checks that the working-directory is correct, and makes sure, that no other instance is
 * running then unpacks the dependencies and starts the actual program.
 * 
 * @author HerrLock
 */
public class Launcher {
    private static final Path lockFile = Paths.get( "temp", ".lock" );

    public static void main( final String... args ) {
        // check if the currrent working directory is valid
        checkWorkingDirectory();
        try {
            // make sure the temp-directory exists
            Path parent = lockFile.getParent();
            if ( parent == null ) {
                throw new IllegalStateException( "Directory is null" );
            }
            Files.createDirectories( parent );
        } catch ( IOException ex ) {
            throw new IllegalStateException( ex );
        }
        // make sure only one instance is running at the same time
        try ( OutputStream lockFileStream = Files.newOutputStream( lockFile, StandardOpenOption.CREATE_NEW,
            StandardOpenOption.DELETE_ON_CLOSE ) ) {
            // check if packed archives exist
            unpackArchives();
            // call "real" main-class after the unpacking was successful
            Main.execute( args );
        } catch ( FileAlreadyExistsException ex ) {
            String message = "The file \"" + ex.getFile() + "\" alredy exists. "
                + "This means, that another process is already running. "
                + "If you are sure this is not the case delete the file manually.";
            throw new IllegalStateException( message, ex );
        } catch ( IOException ex ) {
            throw new IllegalStateException( ex );
        }
    }

    private static void checkWorkingDirectory() {
        try {
            // the folder containing the Launcher-jar
            Path launcherFolderPath = Paths.get( Main.class.getProtectionDomain().getCodeSource().getLocation().toURI() )
                .getParent();
            // the current folder
            Path currentPath = Paths.get( "." ).toAbsolutePath();

            // null-check
            if ( launcherFolderPath == null || currentPath == null ) {
                throw new IllegalStateException( "One folder is null" );
            }
            // check if the files are the same
            boolean isSameFile = Files.isSameFile( launcherFolderPath, currentPath );
            // throw exception if not the same folder
            if ( !isSameFile ) {
                throw new IllegalStateException( "Please navigate to " + launcherFolderPath.toString() );
            }
        } catch ( IOException | URISyntaxException ex ) {
            throw new IllegalStateException( ex );
        }
    }

    /**
     * Unpacks all files ending with .jar.pack.gz with first gzip and second unpack200
     */
    private static void unpackArchives() {
        // check the lib-folder
        Path lib = Paths.get( ".", "lib" );
        // create an unpacker
        Unpacker unpacker = Pack200.newUnpacker();
        // always deflate the unpacked jars
        unpacker.properties().put( Unpacker.DEFLATE_HINT, Unpacker.TRUE );
        // find all ".jar.pack.gz"-files
        try ( DirectoryStream<Path> directoryStream = Files.newDirectoryStream( lib, "*.jar.pack.gz" ) ) {
            // iterate over the packed files
            for ( Path packedFile : directoryStream ) {
                String oldFileName = packedFile.toFile().getName();
                // get the new filename
                String newFileName = oldFileName.replace( ".pack.gz", "" );
                Path newFile = lib.resolve( newFileName );
                try ( GZIPInputStream in = new GZIPInputStream( Files.newInputStream( packedFile ) ) ) {
                    try ( JarOutputStream out = new JarOutputStream( Files.newOutputStream( newFile ) ) ) {
                        // unpack the packed and gzipped file
                        unpacker.unpack( in, out );
                    }
                }
                // delete the old file if unpacking was successful
                Files.delete( packedFile );
            }
        } catch ( IOException ex ) {
            throw new IllegalStateException( "Cannot unpack archives", ex );
        }
    }

}
