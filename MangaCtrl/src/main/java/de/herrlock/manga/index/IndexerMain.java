package de.herrlock.manga.index;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonWriter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.util.JAXBSource;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.google.common.io.ByteStreams;

import de.herrlock.manga.exceptions.MDRuntimeException;
import de.herrlock.manga.index.entity.Index;
import de.herrlock.manga.util.configuration.IndexerConfiguration;

public final class IndexerMain {
    private static final Logger logger = LogManager.getLogger();

    public static void writeIndex( final IndexerConfiguration conf ) {
        try {
            // the index-directory
            final Path indexDir = Files.createDirectories( Paths.get( "index" ) );

            // write data.js
            logger.debug( "Writing index-data" );
            final Path dataJs = indexDir.resolve( "data.js" );
            exportDataJs( dataJs, conf );

            // write index.html
            logger.debug( "Copying index-html" );
            final Path indexHtml = indexDir.resolve( "index.html" );
            copyIndexHtml( indexHtml );

            // unzip DataTables
            logger.debug( "Unzipping DataTables" );
            final Path datatablesDir = Files.createDirectories( indexDir.resolve( "DataTables" ) );
            unzipDatatablesZip( datatablesDir );
        } catch ( IOException ex ) {
            throw new MDRuntimeException( ex );
        }
    }

    public static void exportDataJs( final Path path, final IndexerConfiguration conf ) throws IOException {
        JsonArray jsonArray = Indexer.createJsonIndex( conf );
        try ( BufferedWriter writer = Files.newBufferedWriter( path, StandardCharsets.UTF_8 );
            JsonWriter jsonWriter = Json.createWriter( writer ) ) {
            writer.write( "var data = " );
            jsonWriter.writeArray( jsonArray );
            writer.write( ";" );
        }
    }

    public static void copyIndexHtml( final Path indexHtml ) throws IOException {
        try ( InputStream in = Indexer.class.getResourceAsStream( "index.html" ) ) {
            Files.copy( in, indexHtml, StandardCopyOption.REPLACE_EXISTING );
        }
    }

    public static void unzipDatatablesZip( final Path datatablesDir ) throws IOException {
        try ( ZipInputStream zin = new ZipInputStream( Indexer.class.getResourceAsStream( "DataTables.zip" ) ) ) {
            ZipEntry entry;
            while ( ( entry = zin.getNextEntry() ) != null ) {
                String entryName = entry.getName();
                Path entryPath = datatablesDir.resolve( entryName );
                if ( entry.isDirectory() ) {
                    Files.createDirectories( entryPath );
                } else {
                    Path parent = entryPath.getParent();
                    if ( parent == null ) {
                        throw new IllegalStateException();
                    }
                    Files.createDirectories( parent );
                    InputStream limitedStream = ByteStreams.limit( zin, entry.getSize() );
                    try ( OutputStream targetStream = Files.newOutputStream( entryPath ) ) {
                        ByteStreams.copy( limitedStream, targetStream );
                    }
                    // limitedStream must not be closed, that would close the ZipInputStream
                }
            }
        }
    }

    public static void exportHtmlIndex( final OutputStream out, final IndexerConfiguration conf ) {
        Index index = Indexer.createIndex( conf );

        try ( InputStream xmlDocument = Indexer.class.getResourceAsStream( "manga2datatable.xsl" ) ) {
            Source xsl = new StreamSource( xmlDocument );
            Source xml = new JAXBSource( createMarshaller(), index );
            Result target = new StreamResult( out );
            try {
                Transformer transformer = TransformerFactory.newInstance().newTransformer( xsl );
                transformer.transform( xml, target );
            } catch ( TransformerFactoryConfigurationError | TransformerException ex ) {
                throw new MDRuntimeException( ex );
            }
        } catch ( IOException | JAXBException ex ) {
            throw new MDRuntimeException( ex );
        }
    }

    private static Marshaller createMarshaller() throws JAXBException {
        Marshaller marshaller = JAXBContext.newInstance( Index.class ).createMarshaller();
        marshaller.setProperty( Marshaller.JAXB_FORMATTED_OUTPUT, true );
        return marshaller;
    }

    private IndexerMain() {
        // avoid instantiation
    }

}
