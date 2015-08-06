package de.herrlock.manga.ui.log;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.core.Filter.Result;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;
import org.apache.logging.log4j.core.filter.ThresholdFilter;
import org.apache.logging.log4j.core.layout.PatternLayout;

@Plugin( name = "JTextAreaAppender", category = "Core", elementType = "appender" )
public final class JTextAreaAppender extends AbstractAppender {
    private static final long serialVersionUID = 1L;

    private static final JTextAreaAppender instance = new JTextAreaAppender();

    private JTextAreaAppender() {
        super( "JTextArea", ThresholdFilter.createFilter( Level.INFO, Result.ACCEPT, Result.DENY ),
            PatternLayout.createDefaultLayout() );
    }

    @Override
    public void append( LogEvent event ) {
        String formattedMessage = event.getMessage().getFormattedMessage();
        LogWindow.addMessage( formattedMessage );
    }

    @PluginFactory
    public static JTextAreaAppender getInstance() {
        return instance;
    }

}
