package de.herrlock.manga.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import de.herrlock.javafx.AbstractApplication;
import de.herrlock.javafx.scene.SceneContainer;

public class MDGui extends AbstractApplication {

    public static void main( String[] args ) {
        Application.launch( args );
    }

    @Override
    public void start( Stage stage ) {
        this.setScene( new MDGuiStage() );
        super.start( stage );
    }
}

class MDGuiStage extends SceneContainer {

    public static final ResourceBundle I18N = ResourceBundle.getBundle( "de.herrlock.manga.ui.ui" );

    public MDGuiStage() {
        BorderPane parent = new BorderPane();
        parent.setTop( new Top().getNode() );
        parent.setRight( new Right().getNode() );
        parent.setBottom( new Bottom().getNode() );
        parent.setCenter( new Center().getNode() );
        this.setScene( new Scene( parent ) );
    }

    @Override
    public Collection<String> getStylesheets() {
        return Arrays.asList( "/de/herrlock/manga/ui/style.css" );
    }

    @Override
    public String getTitle() {
        return "MangaDownloader";
    }
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
