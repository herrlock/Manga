package de.herrlock.manga.ui.main;

import static de.herrlock.manga.util.Execs.ADD_TO_JD_W_GUI;
import static de.herrlock.manga.util.Execs.DO_NOTHING;
import static de.herrlock.manga.util.Execs.GUI_DOWNLOADER;
import static de.herrlock.manga.util.Execs.VIEW_PAGE_MAIN;

import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;

import de.herrlock.javafx.handler.Exec;
import de.herrlock.javafx.handler.ExecHandlerTask;
import de.herrlock.manga.exceptions.InitializeException;
import de.herrlock.manga.util.configuration.Configuration;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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

    static final StringProperty urlProperty = new SimpleStringProperty();
    static final StringProperty patternProperty = new SimpleStringProperty();
    static final StringProperty proxyProperty = new SimpleStringProperty();
    static final StringProperty jdhomeProperty = new SimpleStringProperty();

    public static final EventHandler<ActionEvent> START_DOWNLOAD = new EventHandler<ActionEvent>() {
        @Override
        public void handle( ActionEvent event ) {
            handleBtnClick( GUI_DOWNLOADER );
        }
    };
    public static final EventHandler<ActionEvent> EXPORT_TO_JD = new EventHandler<ActionEvent>() {
        @Override
        public void handle( ActionEvent event ) {
            handleBtnClick( ADD_TO_JD_W_GUI );
        }
    };
    public static final EventHandler<ActionEvent> CREATE_HTML = new EventHandler<ActionEvent>() {
        @Override
        public void handle( ActionEvent event ) {
            handleBtnClick( VIEW_PAGE_MAIN );
        }
    };
    public static final EventHandler<ActionEvent> EXIT_GUI = new EventHandler<ActionEvent>() {
        @Override
        public void handle( ActionEvent event ) {
            handleBtnClick( DO_NOTHING );
        }
    };

    public static void handleBtnClick( Exec exec ) {
        new Thread( new ExecHandlerTask( exec ) ).start();
    }

    private MDGuiController() {
        // not used
    }

    public static Properties getProperties() {
        Properties p = new Properties();
        {
            String url = urlProperty.getValueSafe();
            if ( "".equals( url ) ) {
                throw new InitializeException( "No URL is set." );
            }
            p.put( Configuration.URL, url );
        }
        {
            p.put( Configuration.PATTERN, patternProperty.getValueSafe() );
        }
        {
            String proxy = proxyProperty.getValueSafe();
            if ( !"".equals( proxy ) ) {
                p.put( Configuration.PROXY, proxy );
            }
        }
        {
            p.put( Configuration.JDFW, jdhomeProperty.getValueSafe() );
        }
        return p;

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
