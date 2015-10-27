package de.herrlock.manga.downloader;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.swing.JOptionPane;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.javafx.NoWindowApplication;
import de.herrlock.manga.exceptions.MyException;
import de.herrlock.manga.util.Constants;
import de.herrlock.manga.util.CtrlUtils;
import de.herrlock.manga.util.configuration.DownloadConfiguration;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Initializes the download and starts it after confirming the number of pictures and an estimated whole size of the images with a
 * {@link JOptionPane}
 * 
 * @author Herrlock
 */
public final class DialogDownloader extends MDownloader {
    private static final Logger logger = LogManager.getLogger();

    /**
     * the average filesize in kB.<br>
     * estimated from :
     * <dl>
     * <dt>700 chapters of Naruto from Mangapanda</dt>
     * <dd>156.6 kB</dd>
     * <dt>799 chapters of OnePiece from Mangapanda</dt>
     * <dd>183.7 kB</dd>
     * <dt>700 + some coloured chapters of Naruto from Mangafox</dt>
     * <dd>139.2 kB</dd>
     * <dt>799 chapters of OnePiece from Mangafox</dt>
     * <dd>230.6 kB</dd>
     * </dl>
     */
    private static final int avgSize = 177;

    public static class DialogDownloaderApplication extends NoWindowApplication {

        public static void launch( final String... args ) {
            Application.launch( args );
        }

        @Override
        public void start( final Stage stage ) {
            logger.entry();
            Properties p = new Properties();
            // load properties
            try ( InputStream fIn = new FileInputStream( Constants.SETTINGS_FILE ) ) {
                p.load( fIn );
            } catch ( IOException ex ) {
                throw new MyException( ex );
            }
            // properties loaded successful
            DownloadConfiguration conf = DownloadConfiguration.create( p );
            new DialogDownloader( conf ).run();
        }
    }

    public static void main( final String... args ) {
        logger.entry();
        DialogDownloaderApplication.launch( args );
    }

    public DialogDownloader( final DownloadConfiguration conf ) {
        super( conf );
    }

    @Override
    protected void run() {
        logger.entry();
        try {
            if ( goon() ) {
                downloadAll();
            }
        } catch ( Exception ex ) {
            CtrlUtils.showErrorDialog( ex );
            throw ex;
        }
    }

    private boolean goon() {
        String title = "go on?";
        int noOfPictures = getPMCSize();
        int estimatedSize = noOfPictures * avgSize / 1000;
        String message = "Number of pictures: " + noOfPictures + "\n" + "Estimated entire size: " + estimatedSize + " MB";
        int clicked = JOptionPane.showConfirmDialog( null, message, title, JOptionPane.YES_NO_OPTION,
            JOptionPane.INFORMATION_MESSAGE );
        return clicked == JOptionPane.OK_OPTION;
    }

}
