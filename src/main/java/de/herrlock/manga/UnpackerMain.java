package de.herrlock.manga;

import java.io.IOException;
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
        // check if packed archives exist
        unpackArchives();
        // call "real" main-class after the unpacking was successful
        Main.main( args );
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
