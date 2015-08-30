package de.herrlock.manga.ui.log;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoundedRangeModel;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingUtilities;
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

    private static final List<String> lafPrefs = Collections.unmodifiableList( Arrays.asList( "Windows", "Nimbus", "Metal" ) );

    private static final LogWindow instance = new LogWindow();

    private final JFrame frame;
    private final JTextArea textarea = new JTextArea( 30, 70 );
    private final JProgressBar chapterProgressbar = new JProgressBar();
    private final BoundedRangeModel chapterProgressModel = this.chapterProgressbar.getModel();
    private final JProgressBar totalProgressbar = new JProgressBar();
    private final BoundedRangeModel totalProgressModel = this.totalProgressbar.getModel();

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
    }

    public static void setChapterProgressMax( int max ) {
        synchronized ( instance.chapterProgressModel ) {
            instance.chapterProgressModel.setMaximum( max );
        }
    }

    public static void setChapterProgress( int progress ) {
        synchronized ( instance.chapterProgressModel ) {
            instance.chapterProgressModel.setValue( progress );
        }
    }

    public static void setChapterProgressPlusOne() {
        synchronized ( instance.chapterProgressModel ) {
            instance.chapterProgressModel.setValue( instance.chapterProgressModel.getValue() + 1 );
        }
    }

    public static void setProgressMax( int max ) {
        synchronized ( instance.totalProgressModel ) {
            instance.totalProgressModel.setMaximum( max );
        }
    }

    public static void setProgress( int progress ) {
        synchronized ( instance.totalProgressModel ) {
            instance.totalProgressModel.setValue( progress );
        }
    }

    public static void dispose() {
        synchronized ( instance.frame ) {
            final JFrame f = instance.frame;
            SwingUtilities.invokeLater( new Runnable() {
                @Override
                public void run() {
                    f.dispatchEvent( new WindowEvent( f, WindowEvent.WINDOW_CLOSING ) );
                }
            } );
        }
    }

    public static void addMessage( String message ) {
        synchronized ( instance.textarea ) {
            instance.textarea.insert( message + "\n", 0 );
        }
    }

    private LogWindow() {
        this.frame = new JFrame( "Log and Progress" );
        this.frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );

        Container contentPane = this.frame.getContentPane();

        JPanel pbPanel = new JPanel( new BorderLayout( 0, 0 ) );
        pbPanel.add( this.chapterProgressbar, BorderLayout.PAGE_START );
        pbPanel.add( this.totalProgressbar, BorderLayout.PAGE_END );
        contentPane.add( pbPanel, BorderLayout.PAGE_END );

        JScrollPane jScrollPane = new JScrollPane( this.textarea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER );
        contentPane.add( jScrollPane, BorderLayout.PAGE_START );

        this.totalProgressbar.setStringPainted( true );
        this.chapterProgressbar.setStringPainted( true );
        this.frame.pack();
        this.frame.setVisible( true );
    }

}
