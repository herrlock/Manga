package de.herrlock.manga.html;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

import de.herrlock.manga.exceptions.MDRuntimeException;

/**
 * @author HerrLock
 */
public final class ViewPage {
    private static final Logger logger = LogManager.getLogger();

    private final File folder;
    private final Document document;
    private final int maxImgs;

    /**
     * Creates the new ViewPage-instance and prints the html to the destination
     * 
     * @param folder
     *            the folder to save the created files into
     */
    public static void execute( final File folder ) {
        logger.trace( folder );
        Document doc = new ViewPage( folder ).getDocument();
        Path p = new File( folder, "index.html" ).toPath();
        try ( BufferedWriter writer = Files.newBufferedWriter( p, StandardCharsets.UTF_8 ) ) {
            writer.write( "<!DOCTYPE HTML>\n" );
            writer.write( doc.toString() );
        } catch ( final IOException ex ) {
            throw new MDRuntimeException( ex );
        }
    }

    /**
     * Create a new ViewPage in the given {@linkplain File folder}
     * 
     * @param folder
     *            the folder where to create the ViewPages
     */
    private ViewPage( final File folder ) {
        logger.info( "create files in folder {}", folder );
        this.folder = folder;
        this.maxImgs = maxImgs();
        this.document = Document.createShell( "" );
        Element head = this.document.select( "head" ).first();
        createHeadChildren( head );
        Element body = this.document.select( "body" ).first();
        createBodyChildren( body );
    }

    /**
     * @return the Document built
     */
    public Document getDocument() {
        return this.document;
    }

    private String mangaName() {
        String foldername = this.folder.getName();
        String mangarawname = foldername.substring( 0, foldername.lastIndexOf( '_' ) );
        return mangarawname.replace( '_', ' ' );
    }

    private Element createHeadChildren( final Element head ) {
        logger.entry( head );
        head.appendElement( "title" ).text( mangaName() );
        head.appendElement( "meta" ).attr( "charset", "utf-8" );
        head.appendElement( "link" ).attr( "rel", "shortcut icon" ).attr( "href", "favicon.ico" );
        head.appendElement( "link" ).attr( "rel", "stylesheet" ).attr( "href", "style.css" );
        copyFile( "style.css" );

        List<File> files = getChapters();
        File maxFile = Collections.max( files, Const.numericFilenameComparator );
        int max = Integer.parseInt( maxFile.getName() );

        String mangaObject = MessageFormat.format( "var manga = '{' chapter: {0}, max_pages: {1}, chapterblock: {2} '}';", max,
            this.maxImgs, ( max - 1 ) / 10 );
        head.appendElement( "script" ).text( mangaObject );

        String[] js = {
            "jquery-2.1.3.min.js", "onkeydown.js", "main.js"
        };
        for ( String src : js ) {
            head.appendElement( "script" ).attr( "src", src );
            copyFile( src );
        }
        return head;
    }

    private Element createBodyChildren( final Element body ) {
        logger.entry( body );
        body.appendChild( leftDiv() );
        body.appendChild( rightDiv() );
        return body;
    }

    private Element leftDiv() {
        Map<Integer, List<String>> blocks = new HashMap<>();
        {
            // init map
            List<File> files = getSortedChapters( Collections.reverseOrder( Const.numericFilenameComparator ) );
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
        Collections.sort( list, Collections.reverseOrder( Const.integerEntryComparator ) );

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

    private void copyFile( final String filename ) {
        try ( InputStream resource = ViewPage.class.getResourceAsStream( filename ) ) {
            File toFile = new File( this.folder, filename );
            FileUtils.copyInputStreamToFile( resource, toFile );
        } catch ( final IOException ex ) {
            throw new MDRuntimeException( ex );
        }
    }

    private List<File> getSortedChapters( final Comparator<File> comp ) {
        List<File> list = getChapters();
        Collections.sort( list, comp );
        return list;
    }

    private List<File> getChapters() {
        File[] listFiles = this.folder.listFiles( Const.isDirectoryFilter );
        if ( listFiles != null ) {
            return Arrays.asList( listFiles );
        }
        return Arrays.asList();
    }
}

final class Const {
    /**
     * a {@linkplain FileFilter} that filters directories.<br>
     * {@linkplain FileFilter#accept(File)} returns true, if {@linkplain File#isDirectory()} is {@code true}
     */
    static final FileFilter isDirectoryFilter = new FileFilter() {
        @Override
        public boolean accept( final File pathname ) {
            return pathname.isDirectory();
        }
    };

    /**
     * copmares two {@link File}s by their name.<br>
     * tries to use {@linkplain Integer#parseInt(String)} on both names and returns {@linkplain Integer#compare(int, int)}. If
     * that fails it tries {@linkplain Double#parseDouble(String)} and returns {@linkplain Double#compare(double, double)}
     */
    static final Comparator<File> numericFilenameComparator = new Comparator<File>() {
        @Override
        public int compare( final File o1, final File o2 ) {
            String name1 = o1.getName();
            String name2 = o2.getName();
            try {
                int i1 = Integer.parseInt( name1 );
                int i2 = Integer.parseInt( name2 );
                return Integer.compare( i1, i2 );
            } catch ( final NumberFormatException ex ) {
                double d1 = Double.parseDouble( name1 );
                double d2 = Double.parseDouble( name2 );
                return Double.compare( d1, d2 );
            }
        }
    };

    /**
     * compares two {@code Entry<Integer, ?>}-objects by their keys
     */
    static final Comparator<Entry<Integer, ?>> integerEntryComparator = new Comparator<Map.Entry<Integer, ?>>() {
        @Override
        public int compare( final Entry<Integer, ?> o1, final Entry<Integer, ?> o2 ) {
            return o1.getKey().compareTo( o2.getKey() );
        }
    };
}
