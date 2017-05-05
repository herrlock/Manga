package de.herrlock.manga.html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.jar.Attributes;
import java.util.jar.Manifest;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import com.google.common.annotations.VisibleForTesting;

import de.herrlock.manga.exceptions.MDRuntimeException;

/**
 * @author HerrLock
 */
public final class ViewPage {
    private static final Logger logger = LogManager.getLogger();

    private final File folder;
    private final Document document;
    private final int maxImgs;
    private final Collection<String> filesToCopy = new HashSet<>();

    /**
     * Creates the new ViewPage-instance and prints the html to the destination
     * 
     * @param folder
     *            the folder to save the created files into
     */
    public static void execute( final File folder ) {
        logger.trace( folder );
        ViewPage viewPage = new ViewPage( folder );
        Path indexhtml = folder.toPath().resolve( "index.html" );
        try {
            viewPage.saveAt( indexhtml );
            viewPage.copyFiles();
        } catch ( IOException ex ) {
            throw new MDRuntimeException( ex );
        }
    }

    @VisibleForTesting
    static String formatManganame( final String foldername ) {
        String mangarawname;
        if ( foldername.matches( ".+_\\d+" ) ) {
            int lastUnderscore = foldername.lastIndexOf( '_' );
            if ( lastUnderscore > -1 ) {
                mangarawname = foldername.substring( 0, lastUnderscore );
            } else {
                mangarawname = foldername;
            }
        } else {
            mangarawname = foldername;
        }
        return mangarawname.replace( '_', ' ' ).trim();
    }

    /**
     * Create a new ViewPage in the given {@linkplain File folder}
     * 
     * @param folder
     *            the folder where to create the ViewPages
     */
    private ViewPage( final File folder ) {
        logger.trace( folder );
        this.folder = folder;
        this.maxImgs = maxImgs();
        logger.debug( "maxImgs: {}", this.maxImgs );
        this.document = Document.createShell( "" );
        if ( getChapters().isEmpty() ) {
            logger.warn( "The folder does not contain any other folders." );
        } else {
            Element head = this.document.select( "head" ).first();
            createHeadChildren( head );
            Element body = this.document.select( "body" ).first();
            createBodyChildren( body );
        }
    }

    /**
     * @return the Document built
     */
    public Document getDocument() {
        return this.document;
    }

    private String mangaName() {
        String foldername = this.folder.getName();
        return formatManganame( foldername );
    }

    void saveAt( final Path indexhtml ) throws IOException {
        try ( BufferedWriter writer = Files.newBufferedWriter( indexhtml, StandardCharsets.UTF_8 ) ) {
            writer.write( "<!DOCTYPE HTML>\n" );
            writer.write( getDocument().toString() );
        }
    }

    void copyFiles() throws IOException {
        for ( String filename : this.filesToCopy ) {
            Path toFile = this.folder.toPath().resolve( filename );
            logger.info( "copy {} to {}", filename, toFile );
            try ( InputStream resource = ViewPage.class.getResourceAsStream( filename ) ) {
                Files.copy( resource, toFile, StandardCopyOption.REPLACE_EXISTING );
            }
        }
    }

    private Element createHeadChildren( final Element head ) {
        logger.info( "creating head" );
        head.appendElement( "title" ).text( mangaName() );
        head.appendElement( "meta" ).attr( "charset", "utf-8" );
        head.appendElement( "meta" ).attr( "name", "viewport" ).attr( "content", "width=device-width, initial-scale=1.0" );
        head.appendElement( "meta" ).attr( "name", "generator" ).attr( "content", "MangaDownloader v." + getVersion() );
        head.appendElement( "link" ).attr( "rel", "shortcut icon" ).attr( "href", "favicon.ico" );
        head.appendElement( "link" ).attr( "rel", "stylesheet" ).attr( "href", "style.css" );
        this.filesToCopy.add( "style.css" );

        List<File> files = getChapters();
        File maxFile = Collections.max( files, ViewPageConstants.numericFilenameComparator );
        int max = Integer.parseInt( maxFile.getName() );

        String mangaObject = MessageFormat.format(
            "var manga = '{' title: \"{0}\", chapter: +\"{1}\", max_pages: +\"{2}\", chapterblock: +\"{3}\" '}';", mangaName(),
            max, this.maxImgs, ( max - 1 ) / 10 );
        logger.info( "mangaObject: {}", mangaObject );
        head.appendElement( "script" ).text( mangaObject );

        String[] js = {
            "jquery-3.2.1.min.js", "jquery-migrate-3.0.0.js", "main.js"
        };
        for ( String src : js ) {
            head.appendElement( "script" ).attr( "src", src );
            this.filesToCopy.add( src );
        }
        return head;
    }

    private String getVersion() {
        URL[] urls = {
            ViewPage.class.getProtectionDomain().getCodeSource().getLocation()
        };
        Manifest m = new Manifest();
        try ( URLClassLoader urlClassLoader = new URLClassLoader( urls ) ) {
            try ( InputStream in = urlClassLoader.getResourceAsStream( "META-INF/MANIFEST.MF" ) ) {
                m.read( in );
            }
        } catch ( IOException ex ) {
            throw new MDRuntimeException( ex );
        }

        Attributes infoAttributes = m.getAttributes( "Info" );
        return infoAttributes == null ? "DEV" : infoAttributes.getValue( "Version" );
    }

    private Element createBodyChildren( final Element body ) {
        logger.info( "creating body" );
        body.appendChild( leftDiv() );
        body.appendChild( rightDiv() );
        return body;
    }

    private Element leftDiv() {
        logger.info( "creating left div" );
        Map<Integer, List<String>> blocks = new HashMap<>();
        {
            // init map
            List<File> files = getSortedChapters( Collections.reverseOrder( ViewPageConstants.numericFilenameComparator ) );
            for ( File f : files ) {
                String filename = f.getName();
                int blockNr = ( ( int ) Double.parseDouble( filename ) - 1 ) / 10;
                if ( !blocks.containsKey( blockNr ) ) {
                    blocks.put( blockNr, new ArrayList<String>() );
                }
                blocks.get( blockNr ).add( filename );
            }
            logger.debug( "Number of blocks: {}", blocks.size() );
        }
        List<Entry<Integer, List<String>>> list = new ArrayList<>( blocks.entrySet() );
        Collections.sort( list, Collections.reverseOrder( ViewPageConstants.integerEntryComparator ) );

        Element lDiv = new Element( Tag.valueOf( "div" ), "" ).attr( "id", "leftdiv" );
        List<String> firstBlock = list.get( 0 ).getValue();
        double maxChapter = Double.parseDouble( Collections.max( firstBlock ) );
        if ( maxChapter % 10 > 0 ) {
            // in case the first block does not have 10 chapters show them always
            lDiv.appendChild( wrapperDiv( list.remove( 0 ), false ) );
        }
        for ( Entry<Integer, List<String>> e : list ) {
            lDiv.appendChild( wrapperDiv( e, true ) );
        }
        return lDiv;
    }

    private static Element wrapperDiv( final Entry<Integer, List<String>> e, final boolean addHidelink ) {
        Element wrapperDiv = new Element( Tag.valueOf( "div" ), "" );
        Integer blockId = e.getKey();
        if ( addHidelink ) {
            wrapperDiv.appendChild( hidelink( blockId ) );
        }
        Element blockDiv = wrapperDiv.appendElement( "div" ).attr( "id", "block" + blockId );
        Element ul = blockDiv.appendElement( "ul" ).attr( "class", "leftList" );
        List<String> chapternames = e.getValue();
        for ( String c : chapternames ) {
            ul.appendElement( "li" ).appendChild( whitelink( c ) );
        }
        return wrapperDiv;
    }

    private static Element hidelink( final int blockId ) {
        int step = 10;
        int blockName = step * ( blockId + 1 );

        Element a = new Element( Tag.valueOf( "a" ), "" );
        a.attr( "class", "hidelink" );
        a.attr( "id", "hidelink" + blockId );
        a.attr( "href", "javascript:void(0)" );
        a.attr( "onclick", "MangaUtils.show(" + blockId + ")" );
        a.attr( "title", "Zeige Kapitel " + ( blockName - step + 1 ) + " bis " + blockName );

        a.appendElement( "span" ).attr( "id", "arrow" + blockId ).text( " hereComesAnArrow! " );
        a.appendElement( "span" ).text( Integer.toString( blockName ) );
        return a;
    }

    private static Element whitelink( final Object chp ) {
        return whitelink( "", chp, "" );
    }

    private static Element whitelink( final String pretext, final Object chp, final String posttext ) {
        String chpText = String.valueOf( chp );
        Element a = new Element( Tag.valueOf( "a" ), "" );
        a.attr( "class", "whitelink" );
        a.attr( "id", "choose" + chpText );
        a.attr( "href", "javascript:void(0)" );
        a.attr( "onclick", "MangaUtils.choose(" + chpText + ")" );
        a.attr( "title", "Lade Kapitel " + chpText );
        a.text( pretext + chpText + posttext );
        return a;
    }

    private Element rightDiv() {
        logger.info( "creating right div" );
        Element rDiv = new Element( Tag.valueOf( "div" ), "" ).attr( "id", "rightdiv" );
        // pagetitle - current chapternumber
        rDiv.appendElement( "h1" ).attr( "id", "pagetitle" ).text( "If you can read this something BAD happened..." );

        int maxPages = this.maxImgs;
        // page-section - all images
        for ( int i = 1; i <= maxPages; i++ ) {
            Element imgblock = rDiv.appendElement( "div" ).attr( "id", "IMGBlock" + i ).attr( "class", "IMGBlock" );
            imgblock.appendElement( "h2" ).text( ( i < 10 ? "0" : "" ) + i );

            Element imgdiv = imgblock.appendElement( "div" ).attr( "class", "center" );
            imgdiv.appendElement( "img" ).attr( "id", "page" + i ).attr( "class", "image" ).attr( "src", "null.jpg" ).attr( "alt",
                "null" + i + ".jpg" );

            imgblock.appendElement( "hr" ).attr( "class", "separator" );
        }
        // endlink - load next chapter
        rDiv.appendElement( "h1" ).attr( "id", "endlink" ).appendChild( whitelink( "Next" ) );
        return rDiv;
    }

    private int maxImgs() {
        List<File> files = getChapters();
        int maxPages = 0;
        for ( File f : files ) {
            File[] listFiles = f.listFiles();
            maxPages = Math.max( maxPages, listFiles == null ? 0 : listFiles.length );
        }
        return maxPages;
    }

    private List<File> getSortedChapters( final Comparator<File> comp ) {
        List<File> list = getChapters();
        Collections.sort( list, comp );
        return list;
    }

    private List<File> getChapters() {
        File[] listFiles = this.folder.listFiles( ViewPageConstants.isDirectoryFilter );
        if ( listFiles != null ) {
            return Arrays.asList( listFiles );
        }
        return Arrays.asList();
    }
}
