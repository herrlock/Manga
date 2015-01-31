package de.herrlock.manga;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import de.herrlock.manga.downloader.ConsoleDownloader;
import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.Utils;

public class Ctrl {

    public static void main( String[] args ) throws IOException {
        Properties p = new Properties();
        try ( InputStream fIn = new FileInputStream( Constants.SETTINGS_FILE ) ) {
            p.load( fIn );
        }
        setArguments( p );
        ConsoleDownloader.execute();
    }

    public static void setArguments( Properties p ) {
        String[] requiredParameters = new String[] {
            Constants.PARAM_URL
        };
        for ( String s : requiredParameters ) {
            String value = p.getProperty( s );
            if ( value == null || "".equals( value ) ) {
                throw new RuntimeException( "Please fill the field \"" + s + "\" in the file "
                    + new File( Constants.SETTINGS_FILE ).getAbsolutePath() );
            }
        }
        Utils.setArguments( p );
    }
}
