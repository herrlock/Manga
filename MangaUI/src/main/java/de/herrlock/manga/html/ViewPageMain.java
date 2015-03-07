package de.herrlock.manga.html;

import java.io.File;

/**
 * @author HerrLock
 */
public final class ViewPageMain {

    /**
     * 
     * @param folder
     *            the mangafolder to create a viewer for (has the format {@code <manganame>_<timestamp>})
     */
    public static void execute( File folder ) {
        ViewPage.execute( folder );
    }

    /**
     * only fo testing
     * 
     * @param args
     *            unused
     */
    public static void main( String[] args ) {
        execute( new File( "./download/aoki_hagane_no_arpeggio_150306175215" ) );
    }

    private ViewPageMain() {
        // nothing to do
    }
}
