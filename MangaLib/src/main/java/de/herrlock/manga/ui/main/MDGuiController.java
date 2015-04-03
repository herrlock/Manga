package de.herrlock.manga.ui.main;

import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * @author HerrLock
 */
public class MDGuiController {
    public static final ResourceBundle i18n = ResourceBundle.getBundle( "de.herrlock.manga.ui.main.ui" );

    public static final EventHandler<ActionEvent> DO_NOTHING_HANDLER = new EventHandler<ActionEvent>() {
        @Override
        public void handle( ActionEvent arg0 ) {
            System.out.println( "Action not implemented" );
        }
    };

}
