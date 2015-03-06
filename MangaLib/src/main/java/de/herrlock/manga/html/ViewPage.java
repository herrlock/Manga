package de.herrlock.manga.html;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileFilter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;

public class ViewPage {
    private static final Charset UTF8 = StandardCharsets.UTF_8;

    public static void execute( File folder ) {
        Document doc = new ViewPage( folder ).getDocument();
        Path p = new File( folder, "index.html" ).toPath();
        try ( BufferedWriter writer = Files.newBufferedWriter( p, UTF8 ) ) {
            writer.write( "<!DOCTYPE HTML>\n" );
            writer.write( doc.toString() );
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }

    private final File folder;
    private final Document document;

    public ViewPage( File folder ) {
        this.folder = folder;
        this.document = new Document( "" );
        this.document.appendChild( createHead() );
        this.document.appendChild( createBody() );
    }

    public Document getDocument() {
        return this.document;
    }

    private String mangaName() {
        String foldername = this.folder.getName();
        String mangarawname = foldername.substring( 0, foldername.lastIndexOf( '_' ) );
        return mangarawname.replace( '_', ' ' );
    }

    private Element createHead() {
        Element head = new Element( Tag.valueOf( "head" ), "" );
        head.appendElement( "title" ).text( mangaName() );
        head.appendElement( "meta" ).attr( "charset", "utf-8" );
        head.appendElement( "link" ).attr( "rel", "shortcut icon" ).attr( "href", "favicon.ico" );
        head.appendElement( "link" ).attr( "rel", "stylesheet" ).attr( "href", "style.css" );
        copyFile( "style.css" );
        String[] js = {
            "jquery-2.1.3.min.js", "onkeydown.js", "main.js"
        };
        for ( String src : js ) {
            head.appendElement( "script" ).attr( "src", src );
            copyFile( src );
        }
        return head;
    }

    private Element createBody() {
        Element body = new Element( Tag.valueOf( "body" ), "" );
        body.attr( "onload", "init()" );
        body.appendChild( leftDiv() );
        body.appendChild( rightDiv() );
        return body;
    }

    private Element leftDiv() {
        Map<Integer, List<String>> blocks = new HashMap<>();
        {
            List<File> files = Arrays.asList( getChapters() );
            Collections.sort( files, Collections.reverseOrder( numericFilenameComparator ) );
            for ( File f : files ) {
                String filename = f.getName();
                int blockNr = ( ( int ) Double.parseDouble( filename ) - 1 ) / 10;
                if ( !blocks.containsKey( blockNr ) ) {
                    blocks.put( blockNr, new ArrayList<String>() );
                }
                blocks.get( blockNr ).add( filename );
            }
        }
        List<Entry<Integer, List<String>>> list = new ArrayList<>( blocks.entrySet() );
        Collections.sort( list, Collections.reverseOrder( integerEntryComparator ) );

        Element lDiv = new Element( Tag.valueOf( "div" ), "" ).attr( "id", "leftdiv" );

        for ( Entry<Integer, List<String>> e : list ) {
            Element wrapperDiv = lDiv.appendElement( "div" );
            int blockId = e.getKey();
            List<String> chapternames = e.getValue();
            wrapperDiv.appendChild( hidelink( blockId ) );
            Element blockDiv = wrapperDiv.appendElement( "div" ).attr( "id", "block" + blockId );
            for ( String c : chapternames ) {
                blockDiv.appendChild( whitelink( c ) );
            }
        }
        return lDiv;
    }

    private static Element hidelink( int blockId ) {
        int step = 10;
        int blockName = step * ( blockId + 1 );

        Element a = new Element( Tag.valueOf( "a" ), "" );
        a.attr( "class", "hidelink" );
        a.attr( "id", "hidelink" + blockId );
        a.attr( "href", "javascript:void(0)" );
        a.attr( "onclick", "show(" + blockId + ")" );
        a.attr( "title", "Zeige Kapitel " + ( blockName - step + 1 ) + " bis " + blockName );

        a.appendElement( "span" ).attr( "id", "arrow" + blockId ).text( " hereComesAnArrow! " );
        a.appendElement( "span" ).text( "" + blockName );
        return a;
    }

    private static Element whitelink( Object chp ) {
        return whitelink( "", chp, "" );
    }

    private static Element whitelink( String pretext, Object chp, String posttext ) {
        String chpText = String.valueOf( chp );
        Element a = new Element( Tag.valueOf( "a" ), "" );
        a.attr( "class", "whitelink" );
        a.attr( "id", "choose" + chpText );
        a.attr( "href", "javascript:void(0)" );
        a.attr( "onclick", "choose(" + chpText + ")" );
        a.attr( "title", "Lade Kapitel " + chpText );
        a.text( pretext + chpText + posttext );
        return a;
    }

    private Element rightDiv() {
        Element rDiv = new Element( Tag.valueOf( "div" ), "" ).attr( "id", "rightdiv" );
        // pagetitle - current chapternumber
        rDiv.appendElement( "h1" ).attr( "id", "pagetitle" ).text( "If you can read this something BAD happened..." );

        File[] files = getChapters();
        int maxPages = 0;
        for ( File f : files ) {
            maxPages = Math.max( maxPages, f.listFiles().length );
        }
        // page-section - all images
        for ( int i = 1; i <= maxPages; i++ ) {
            Element imgblock = rDiv.appendElement( "div" ).attr( "id", "IMGBlock" + i ).attr( "class", "IMGBlock" );
            imgblock.appendElement( "h2" ).text( ( i < 10 ? "0" : "" ) + i );

            Element imgdiv = imgblock.appendElement( "div" ).attr( "style", "text-align:center" );
            imgdiv.appendElement( "img" ).attr( "id", "page" + i ).attr( "class", "image" ).attr( "src", "null.jpg" )
                .attr( "alt", "null" + i + ".jpg" );
        }
        // endlink - load next chapter
        rDiv.appendElement( "h1" ).attr( "id", "endlink" ).text( "Link to the next chapter" );
        return rDiv;
    }

    private void copyFile( String filename ) {
        try {
            List<String> readLines = new ArrayList<>();

            InputStream resource = ViewPage.class.getResourceAsStream( filename );
            try ( BufferedReader reader = new BufferedReader( new InputStreamReader( resource, UTF8 ) ) ) {
                String nextline;
                while ( ( nextline = reader.readLine() ) != null ) {
                    readLines.add( nextline );
                }
            }

            FileOutputStream out = new FileOutputStream( new File( this.folder, filename ) );
            try ( BufferedWriter writer = new BufferedWriter( new OutputStreamWriter( out, UTF8 ) ) ) {
                for ( String s : readLines ) {
                    writer.write( s + '\n' );
                }
            }
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }
    }

    private File[] getChapters() {
        return this.folder.listFiles( isDirectoryFilter );
    }

    private static final FileFilter isDirectoryFilter = new FileFilter() {
        @Override
        public boolean accept( File pathname ) {
            return pathname.isDirectory();
        }
    };

    private static final Comparator<File> numericFilenameComparator = new Comparator<File>() {
        @Override
        public int compare( File o1, File o2 ) {
            String name1 = o1.getName();
            String name2 = o2.getName();
            try {
                int i1 = Integer.parseInt( name1 );
                int i2 = Integer.parseInt( name2 );
                return Integer.compare( i1, i2 );
            } catch ( NumberFormatException ex ) {
                double d1 = Double.parseDouble( name1 );
                double d2 = Double.parseDouble( name2 );
                return Double.compare( d1, d2 );
            }
        }
    };

    private static final Comparator<Entry<Integer, ?>> integerEntryComparator = new Comparator<Map.Entry<Integer, ?>>() {
        @Override
        public int compare( Entry<Integer, ?> o1, Entry<Integer, ?> o2 ) {
            return o1.getKey().compareTo( o2.getKey() );
        }
    };
}
