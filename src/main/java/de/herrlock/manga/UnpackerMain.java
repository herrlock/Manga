package de.herrlock.manga;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Unpacker;
import java.util.zip.GZIPInputStream;

/**
 * Entrance class for the jar, that first unpacks the dependencies.
 * 
 * @author HerrLock
 */
final class UnpackerMain {
    public static void main( final String... args ) {
        // check if the currrent working directory is valid
        checkWorkingDirectory();
        // check if packed archives exist
        unpackArchives();
        // call "real" main-class after the unpacking was successful
        Main.main( args );
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

    /**
     * Private constructor to avoid instantiation
     */
    private UnpackerMain() {
        // not used
    }
}
