package de.herrlock.manga.ui.main;

import static de.herrlock.manga.util.Execs.DO_NOTHING;
import static de.herrlock.manga.util.Execs.VIEW_PAGE_MAIN;

import java.util.List;
import java.util.Properties;

import de.herrlock.javafx.handler.Exec;
import de.herrlock.javafx.handler.ExecHandlerTask;
import de.herrlock.manga.downloader.ExtDownloader;
import de.herrlock.manga.exceptions.InitializeException;
import de.herrlock.manga.host.Hoster;
import de.herrlock.manga.host.Hosters;
import de.herrlock.manga.jd.JDExport;
import de.herrlock.manga.util.configuration.Configuration;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * @author HerrLock
 */
public final class MDGuiController {
    @FXML
    final StringProperty url = new SimpleStringProperty(), pattern = new SimpleStringProperty(),
        proxy = new SimpleStringProperty(), jdhome = new SimpleStringProperty();

    @FXML
    private GridPane rightGridPane;
    @FXML
    private ScrollPane rightScrollPane;
    @FXML
    private VBox rightVBox;

    public void startDownload() {
        handleBtnClick( new Exec() {
            @Override
            public void execute() {
                Properties p = getProperties();
                ExtDownloader.execute( p );
            }
        } );
    }

    public void exportToJd() {
        handleBtnClick( new Exec() {
            @Override
            public void execute() {
                Properties p = getProperties();
                JDExport.execute( p );
            }
        } );
    }

    public void createHtml() {
        handleBtnClick( VIEW_PAGE_MAIN );
    }

    public void exitGui() {
        handleBtnClick( DO_NOTHING );
    }

    public void handleBtnClick( Exec exec ) {
        new Thread( new ExecHandlerTask( exec ) ).start();
    }

    public void setUrl( String url ) {
        this.url.set( url );
    }

    public String getUrl() {
        return this.url.get();
    }

    public StringProperty urlProperty() {
        return this.url;
    }

    public void setPattern( String pattern ) {
        this.pattern.set( pattern );
    }

    public String getPattern() {
        return this.pattern.get();
    }

    public StringProperty patternProperty() {
        return this.pattern;
    }

    public void setPropxy( String proxy ) {
        this.proxy.set( proxy );
    }

    public String getProxy() {
        return this.proxy.get();
    }

    public StringProperty proxyProperty() {
        return this.proxy;
    }

    public void setJdhome( String jdhome ) {
        this.jdhome.set( jdhome );
    }

    public String getJdhome() {
        return this.jdhome.get();
    }

    public StringProperty jdhomeProperty() {
        return this.jdhome;
    }

    public Properties getProperties() {
        Properties p = new Properties();
        {
            String url1 = this.url.getValueSafe();
            if ( "".equals( url1 ) ) {
                throw new InitializeException( "No URL is set." );
            }
            p.put( Configuration.URL, url1 );
        }
        {
            p.put( Configuration.PATTERN, this.pattern.getValueSafe() );
        }
        {
            String proxy1 = this.proxy.getValueSafe();
            if ( !"".equals( proxy1 ) ) {
                p.put( Configuration.PROXY, proxy1 );
            }
        }
        {
            p.put( Configuration.JDFW, this.jdhome.getValueSafe() );
        }
        return p;
    }

    public void initialize() {
        this.rightScrollPane.prefViewportWidthProperty().bind( this.rightVBox.widthProperty() );
        List<Hoster> values = Hosters.sortedValues();
        for ( int y = 0; y < values.size(); y++ ) {
            Hoster hoster = values.get( y );
            String hostname = hoster.getName();
            this.rightGridPane.add( new Text( hostname ), 0, y );
            String hosturl = hoster.getBaseUrl().getHost().substring( 4 );
            this.rightGridPane.add( new Text( hosturl ), 1, y );
        }
    }

}

final class EmptyListener implements ChangeListener<String> {
    public static final String ISEMPTY = "isEmpty";
    private final TextField textField;

    public EmptyListener( TextField textField ) {
        this.textField = textField;
    }

    @Override
    public void changed( ObservableValue<? extends String> obsValue, String string1, String string2 ) {
        List<String> classes = this.textField.getStyleClass();
        if ( string2.trim().isEmpty() ) {
            classes.add( ISEMPTY );
        } else {
            classes.remove( ISEMPTY );
        }
    }
}
