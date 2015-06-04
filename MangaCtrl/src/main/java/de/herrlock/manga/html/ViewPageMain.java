package de.herrlock.manga.html;

import java.io.File;

import javax.swing.JOptionPane;

/**
 * @author HerrLock
 */
public final class ViewPageMain {

    public static void main( String... args ) {
        execute();
    }

    public static void execute() {
        String input = JOptionPane.showInputDialog( "Input the folder where the chapters are." );
        if ( input != null ) {
            File folder = new File( input );
            if ( folder.exists() ) {
                ViewPage.execute( folder );
            } else {
                JOptionPane.showMessageDialog( null, "Folder does not exists." );
            }
        }
    }

    /**
     * @param folder
     *            the mangafolder to create a viewer for (has the format {@code <manganame>_<timestamp>})
     */
    public static void execute( File folder ) {
        ViewPage.execute( folder );
    }

    private ViewPageMain() {
        // nothing to do
    }
}
