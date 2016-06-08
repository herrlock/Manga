package de.herrlock.manga.ui.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;

/**
 * an Appender for log4j writing the logged messages to the {@link LogWindow}'s {@link javax.swing.JTextArea}
 * 
 * @author HerrLock
 */
@Plugin( name = "JTextAreaAppender", category = "Manga", elementType = "appender" )
public final class JTextAreaAppender extends AbstractAppender {
    private static final JTextAreaAppender instance = new JTextAreaAppender();

    private JTextAreaAppender() {
        super( "JTextArea", ThresholdFilter.createFilter( Level.INFO, Result.ACCEPT, Result.DENY ),
            PatternLayout.createDefaultLayout() );
    }

    @Override
    public void append( final LogEvent event ) {
        String formattedMessage = event.getMessage().getFormattedMessage();
        LogWindow.addMessage( formattedMessage );
    }

    /**
     * @return the instance of this Appender to avoid multiple instances
     */
    @PluginFactory
    public static JTextAreaAppender getInstance() {
        return instance;
    }

}
