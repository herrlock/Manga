package de.herrlock.manga.ui;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.event.WindowEvent;
import java.util.logging.Formatter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

import javax.swing.JFrame;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.ScrollPaneConstants;
import javax.swing.WindowConstants;

/**
 * @author HerrLock
 */
public class LogWindow extends Handler {

    private final JFrame frame = new JFrame();
    private final JTextArea textarea = new JTextArea( 40, 60 );
    private final JProgressBar progressbar = new JProgressBar();

    public LogWindow() {
        this.frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        Container contentPane = this.frame.getContentPane();
        JScrollPane jScrollPane = new JScrollPane( this.textarea, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,
            ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED );
        contentPane.add( jScrollPane, BorderLayout.PAGE_START );
        contentPane.add( this.progressbar, BorderLayout.PAGE_END );
        this.progressbar.setStringPainted( true );
        this.frame.pack();
    }

    public void show() {
        this.frame.setVisible( true );
    }

    public void print( String s ) {
        String previous = this.textarea.getText();
        this.textarea.setText( s + previous );
    }

    public void println( String s ) {
        this.print( s );
        this.print( "\n" );
    }

    public void setProgress( final int progress ) {
        this.progressbar.setValue( progress );
    }

    public void setProgressMax( final int max ) {
        this.progressbar.setMaximum( max );
    }

    public void setTitle( String title ) {
        this.frame.setTitle( title );
    }

    @Override
    public void publish( LogRecord record ) {
        Formatter formatter = this.getFormatter();
        if ( formatter != null ) {
            String entry = formatter.format( record );
            this.println( entry );
        }
    }

    @Override
    public void flush() {
        // output is not buffered
    }

    @Override
    public void close() throws SecurityException {
        WindowEvent evt = new WindowEvent( this.frame, WindowEvent.WINDOW_CLOSED );
        this.frame.dispatchEvent( evt );
    }
}
