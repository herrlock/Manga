package de.herrlock.manga.ui.main;

import java.util.List;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.control.TextField;

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

final class EmptyListener implements ChangeListener<String> {
    private final TextField textField;

    public EmptyListener( TextField textField ) {
        this.textField = textField;
    }

    @Override
    public void changed( ObservableValue<? extends String> obsValue, String string1, String string2 ) {
        List<String> classes = this.textField.getStyleClass();
        if ( string2.trim().isEmpty() ) {
            classes.add( CCN.ISEMPTY );
        } else {
            classes.remove( CCN.ISEMPTY );
        }
    }
}

/**
 * CSS-Classname constants
 */
final class CCN {

    public static final String MAGENTA = "magenta";
    public static final String RED = "red";
    public static final String YELLOW = "yellow";
    public static final String GREEN = "green";
    public static final String BLUE = "blue";
    public static final String CYAN = "cyan";
    public static final String GREY = "grey";

    public static final String GRIDPANE = "gridpane";
    public static final String TEXT = "text";

    public static final String ISEMPTY = "isEmpty";

    public static final String PADDING_8 = "padding8";
    public static final String PADDING_16 = "padding16";

    private CCN() {
        // not used
    }
}
