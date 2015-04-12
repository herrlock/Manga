package de.herrlock.manga.jd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Crawljob {

    private final File folder;
    private final String packagenumber;
    private final List<CrawljobEntry> entries = Collections.synchronizedList( new ArrayList<CrawljobEntry>() );

    /**
     * creates a crealjob-file to fil with entries
     * 
     * @param folder
     *            the folder where to download the images to (relative path, eg. "./manganame_timestamp/01")
     * @param packagenumber
     *            name of the JDownloader-package
     */
    public Crawljob( File folder, String packagenumber ) {
        this.folder = folder;
        this.packagenumber = packagenumber;
    }

    /**
     * adds a resource to this Crawljob
     * 
     * @param filename
     *            the target name for the resource
     * @param url
     *            the resource's URL
     */
    public void addCrawljobEntry( String filename, String url ) {
        synchronized ( this.entries ) {
            this.entries.add( new CrawljobEntry( filename, url ) );
        }
    }

    /**
     * returns the filename that the current Crawljob should be written to
     * 
     * @return the preferred filename for the current Crawljob
     */
    public String getFilename() {
        return this.packagenumber + ".crawljob";
    }

    /**
     * creates a String that represents the current Crawljob
     * 
     * @return string for the current Crawljob-object
     */
    public String export() {
        String ls = System.lineSeparator();
        StringBuilder sb = new StringBuilder();
        for ( CrawljobEntry c : this.entries ) {
            sb.append( ls );
            sb.append( "->NEW ENTRY<-" );
            sb.append( ls );
            sb.append( c.export() );
            sb.append( ls );
            sb.append( "downloadFolder=" );
            sb.append( this.folder.getAbsolutePath() );
            sb.append( ls );
            sb.append( "packageName=" );
            sb.append( this.packagenumber );
            sb.append( ls );
            sb.append( "addOfflineLink=true" );
            sb.append( ls );
            sb.append( ( char ) 0 );
        }
        return sb.toString();
    }

    private static class CrawljobEntry {
        private final String filename;
        private final String url;

        /**
         * creates a CrawljobEntry or the resource with the given filename at the given URL
         * 
         * @param filename
         * @param url
         */
        public CrawljobEntry( String filename, String url ) {
            this.filename = filename;
            this.url = url;
        }

        /**
         * creates a String that represents the current CrawljobEntry
         * 
         * @return string for the current CrawljobEntry-object
         */
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
