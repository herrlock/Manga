package de.herrlock.manga.ui.log;

import java.awt.BorderLayout;
import java.awt.Container;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

/**
 * @author HerrLock
 */
public final class LogWindow {
    private static final LogWindow instance = new LogWindow();

    private final JFrame frame = new JFrame();
    private final JTextArea textarea = new JTextArea( 40, 60 );
    private final JProgressBar progressbar = new JProgressBar();

    public static void setProgressMax( int max ) {
        instance.progressbar.setMaximum( max );
    }

    public static void setProgress( int progress ) {
        instance.progressbar.setValue( progress );
    }

    public static void dispose() {
        instance.frame.dispose();
    }

    public static void addMessage( String message ) {
        instance.textarea.insert( message + "\n", 0 );
    }

    private LogWindow() {
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
