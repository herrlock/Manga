package de.herrlock.manga.ui.log;

import java.awt.BorderLayout;
import java.awt.Container;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.WindowConstants;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * @author HerrLock
 */
public final class LogWindow {
    private static final Logger logger = LogManager.getLogger();

    private static final List<String> lafPrefs = Collections
        .unmodifiableList( Arrays.asList( "Windows Classic", "Windows", "Nimbus", "Metal" ) );

    private static final LogWindow instance = new LogWindow();

    private final JFrame frame;
    private final JTextArea textarea = new JTextArea( 30, 70 );
    private final JProgressBar progressbar = new JProgressBar();

    static {
        // try to set a LookAndFeel
        Map<String, LookAndFeelInfo> lookAndFeels = new HashMap<>( lafPrefs.size() );
        for ( LookAndFeelInfo lookAndFeelInfo : UIManager.getInstalledLookAndFeels() ) {
            lookAndFeels.put( lookAndFeelInfo.getName(), lookAndFeelInfo );
        }
        for ( String s : lafPrefs ) {
            if ( lookAndFeels.containsKey( s ) ) {
                try {
                    LookAndFeelInfo lookAndFeelInfo = lookAndFeels.get( s );
                    logger.debug( "set LoolAndFeel to {}", lookAndFeelInfo );
                    UIManager.setLookAndFeel( lookAndFeelInfo.getClassName() );
                    break;
                } catch ( ReflectiveOperationException | UnsupportedLookAndFeelException ex ) {
                    // do nothing, try the next preference
                    logger.warn( "LookAndFeel {} not found", s );
                }
            }
        }
        System.out.println( UIManager.getLookAndFeel().getName() );

    }

    public static void setProgressMax( int max ) {
        synchronized ( instance.progressbar ) {
            instance.progressbar.setMaximum( max );
        }
    }

    public static void setProgress( int progress ) {
        synchronized ( instance.progressbar ) {
            instance.progressbar.setValue( progress );
        }
    }

    public static void dispose() {
        synchronized ( instance.frame ) {
            instance.frame.dispose();
        }
    }

    public static void addMessage( String message ) {
        synchronized ( instance.textarea ) {
            instance.textarea.insert( message + "\n", 0 );
        }
    }

    private LogWindow() {
        this.frame = new JFrame( "Log and Progress" );
        this.frame.setDefaultCloseOperation( WindowConstants.DISPOSE_ON_CLOSE );

        Container contentPane = this.frame.getContentPane();
        contentPane.add( this.progressbar, BorderLayout.PAGE_END );
        JScrollPane jScrollPane = new JScrollPane( this.textarea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        contentPane.add( jScrollPane, BorderLayout.PAGE_START );

        this.progressbar.setStringPainted( true );
        this.frame.pack();
        this.frame.setVisible( true );
    }

}
