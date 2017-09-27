package de.herrlock.manga.viewpage;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.Arrays;
import java.util.Collection;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith( Parameterized.class )
public class TestViewPage {

    private static final Path testfolder = Paths.get( ".", "testFolder" );
    private final int numberOfChapters;
    private final Path baseFolder;

    @Parameters( name = "{0}: {1}" )
    public static Collection<Object[]> createParams() {
        return Arrays.asList( new Object[][] {
            {
                42, "testDefault"
            }, {
                200, "testLarge"
            }, {
                0, "testEmpty"
            }
        } );
    }

    public TestViewPage( final int numberOfChapters, final String baseFolder ) {
        this.numberOfChapters = numberOfChapters;
        this.baseFolder = testfolder.resolve( baseFolder );
    }

    @Before
    public void setUp() throws IOException {
        Files.createDirectories( this.baseFolder );
        for ( int i = 1; i <= this.numberOfChapters; i++ ) {
            Path newFolder = this.baseFolder.resolve( Integer.toString( i ) );
            if ( Files.notExists( newFolder ) ) {
                Files.createDirectories( newFolder );
            }
            Path newFile = newFolder.resolve( "tmp" );
            if ( Files.notExists( newFile ) ) {
                Files.createFile( newFile );
            }
        }
    }

    @After
    public void deleteFolders() throws IOException {
        Files.walkFileTree( this.baseFolder, new DeletingFileVisitor() );
    }

    @Test
    public void runViewPage() {
        File file = this.baseFolder.toFile();
        ViewPage.execute( file );
    }

    private static final class DeletingFileVisitor extends SimpleFileVisitor<Path> {
        @Override
        public FileVisitResult visitFile( final Path file, final BasicFileAttributes attrs ) throws IOException {
            Files.delete( file );
            return FileVisitResult.CONTINUE;
        }
        @Override
        public FileVisitResult postVisitDirectory( final Path dir, final IOException exc ) throws IOException {
            Files.delete( dir );
            return FileVisitResult.CONTINUE;
        }
    }

}
