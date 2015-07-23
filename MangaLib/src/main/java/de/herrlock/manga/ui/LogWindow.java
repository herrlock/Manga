package de.herrlock.manga.ui;

import java.io.PrintStream;

import javax.swing.JFrame;
import javax.swing.JTextArea;
import javax.swing.WindowConstants;

/**
 * @author HerrLock
 */
public class LogWindow {
    final JFrame frame = new JFrame();
    final JTextArea textarea = new JTextArea();

    public LogWindow() {
        this.frame.setDefaultCloseOperation( WindowConstants.EXIT_ON_CLOSE );
        this.frame.getContentPane().add( this.textarea );
    }

    public void print( String s ) {
        String previous = this.textarea.getText();
        this.textarea.setText( previous + s );
    }

    public void println( String s ) {
        this.print( s );
        this.print( "\n" );
    }

    public void setTitle( String title ) {
        this.frame.setTitle( title );
    }
}
