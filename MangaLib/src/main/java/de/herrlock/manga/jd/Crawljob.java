package de.herrlock.manga.jd;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public class Crawljob {

    private static final File CJ = new File( "./crawljobs/" );

    public static void main( String[] args ) throws Exception {
        Crawljob c1 = new Crawljob( new File( "./lhtest_0815/00" ), "1" );
        c1.addCrawljob( "04", "http://a.mfcdn.net/store/manga/13229/01-000.0/compressed/d004.jpg" );
        c1.addCrawljob( "05", "http://a.mfcdn.net/store/manga/13229/01-000.0/compressed/d005.jpg" );
        try ( FileOutputStream out = new FileOutputStream( new File( CJ, "./1.crawljob" ) ) ) {
            out.write( c1.export().getBytes( StandardCharsets.UTF_8 ) );
        }
        Crawljob c2 = new Crawljob( new File( "./lhtest_0815/01" ), "2" );
        c2.addCrawljob( "07", "http://a.mfcdn.net/store/manga/13229/01-001.0/compressed/j007.jpg" );
        c2.addCrawljob( "08", "http://a.mfcdn.net/store/manga/13229/01-001.0/compressed/j008.jpg" );
        try ( FileOutputStream out = new FileOutputStream( new File( CJ, "./2.crawljob" ) ) ) {
            out.write( c2.export().getBytes( StandardCharsets.UTF_8 ) );
        }
    }

    private final File folder;
    private final String packagenumber;
    private final List<CrawljobEntry> entries = new ArrayList<>();

    /**
     * relative path eg. "./manganame_timestamp/01"
     * 
     * @param folder
     */
    public Crawljob( File folder, String packagenumber ) {
        this.folder = folder;
        this.packagenumber = packagenumber;
    }

    public void addCrawljob( String filename, String url ) {
        this.entries.add( new CrawljobEntry( filename, url ) );
    }

    public String getFilename() {
        return this.packagenumber + ".crawljob";
    }

    public String export() {
        String ls = System.lineSeparator();
        StringBuilder sb = new StringBuilder( ls );
        sb.append( ls );
        for ( CrawljobEntry c : this.entries ) {
            sb.append( c.export() );
            sb.append( ls );
            sb.append( "downloadFolder=" );
            sb.append( this.folder.getAbsolutePath() );
            sb.append( ls );
            sb.append( "packageName=" );
            sb.append( this.packagenumber );
            sb.append( ls );
            sb.append( ls );
        }
        return sb.toString();
    }

    private static class CrawljobEntry {
        private final String filename;
        private final String url;

        public CrawljobEntry( String filename, String url ) {
            this.filename = filename;
            this.url = url;
        }

        public String export() {
            StringBuilder sb = new StringBuilder();
            sb.append( "text=" );
            sb.append( this.url );
            sb.append( System.lineSeparator() );
            sb.append( "filename=" );
            sb.append( this.filename );
            sb.append( ".png" );
            return sb.toString();
        }
    }
}
