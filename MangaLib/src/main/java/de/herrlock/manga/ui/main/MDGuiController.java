package de.herrlock.manga.ui.main;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;

/**
 * @author Jan Rau
 *
 */
public class MDGuiController {

    public static final EventHandler<ActionEvent> DO_NOTHING_HANDLER = new EventHandler<ActionEvent>() {
        @Override
        public void handle( ActionEvent arg0 ) {
            System.out.println( "Action not implemented" );
        }
    };

}
