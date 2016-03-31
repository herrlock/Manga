package de.herrlock.manga.jd;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A class representing a single Crawljob-file that contains a whole Chapter
 * 
 * @author HerrLock
 */
public final class Crawljob {

    private final File folder;
    private final String packagenumber;
    private final List<CrawljobEntry> entries = Collections.synchronizedList( new ArrayList<CrawljobEntry>() );

    /**
     * creates a crawljob-file to fill with entries
     * 
     * @param folder
     *            the folder where to download the images to (relative path, eg. "./manganame_timestamp/01")
     * @param packagenumber
     *            name of the JDownloader-package
     */
    public Crawljob( final File folder, final String packagenumber ) {
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
    public void addCrawljobEntry( final String filename, final String url ) {
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
            sb.append( ls )//
                .append( "->NEW ENTRY<-" )//
                .append( ls )//
                .append( c.export() )//
                .append( ls )//
                .append( "downloadFolder=" )//
                .append( this.folder.getAbsolutePath() )//
                .append( ls )//
                .append( "packageName=" )//
                .append( this.packagenumber )//
                .append( ls )//
                .append( "addOfflineLink=true" )//
                .append( ls )//
                .append( ( char ) 0 );
        }
        return sb.toString();
    }
}
