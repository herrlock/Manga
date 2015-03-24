package de.herrlock.manga.jd;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class Crawljob {

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
