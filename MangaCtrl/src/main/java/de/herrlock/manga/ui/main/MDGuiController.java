package de.herrlock.manga.ui.main;

import static de.herrlock.manga.util.Execs.DO_NOTHING;
import static de.herrlock.manga.util.Execs.VIEW_PAGE_MAIN;

import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.SortedSet;
import java.util.concurrent.atomic.AtomicInteger;

import de.herrlock.javafx.handler.Exec;
import de.herrlock.javafx.handler.ExecHandlerTask;
import de.herrlock.manga.downloader.DownloadProcessor;
import de.herrlock.manga.downloader.impl.PlainDownloader;
import de.herrlock.manga.exceptions.InitializeException;
import de.herrlock.manga.host.Hoster;
import de.herrlock.manga.host.Hosters;
import de.herrlock.manga.util.configuration.Configuration;
import de.herrlock.manga.util.configuration.DownloadConfiguration;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

/**
 * @author HerrLock
 */
public final class MDGuiController implements Initializable {

    @FXML
    private final StringProperty url = new SimpleStringProperty();
    @FXML
    private final StringProperty pattern = new SimpleStringProperty();
    @FXML
    private final StringProperty proxy = new SimpleStringProperty();

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
                DownloadConfiguration conf = DownloadConfiguration.create( p );
                DownloadProcessor.getInstance().addDownload( new PlainDownloader( conf ) );
            }
        } );
    }

    public void createHtml() {
        handleBtnClick( VIEW_PAGE_MAIN );
    }

    public void exitGui() {
        handleBtnClick( DO_NOTHING );
    }

    public void handleBtnClick( final Exec exec ) {
        new Thread( new ExecHandlerTask( exec ) ).start();
    }

    public void setUrl( final String url ) {
        this.url.set( url );
    }

    public String getUrl() {
        return this.url.get();
    }

    public StringProperty urlProperty() {
        return this.url;
    }

    public void setPattern( final String pattern ) {
        this.pattern.set( pattern );
    }

    public String getPattern() {
        return this.pattern.get();
    }

    public StringProperty patternProperty() {
        return this.pattern;
    }

    public void setProxy( final String proxy ) {
        this.proxy.set( proxy );
    }

    public String getProxy() {
        return this.proxy.get();
    }

    public StringProperty proxyProperty() {
        return this.proxy;
    }

    public Properties getProperties() {
        Properties p = new Properties();
        {
            String url1 = this.url.getValueSafe();
            if ( "".equals( url1 ) || url1.trim().isEmpty() ) {
                throw new InitializeException( "No URL is set." );
            }
            p.put( Configuration.URL, url1 );
        }
        {
            p.put( Configuration.PATTERN, this.pattern.getValueSafe() );
        }
        {
            String proxy1 = this.proxy.getValueSafe();
            if ( !"".equals( proxy1 ) && !proxy1.trim().isEmpty() ) {
                p.put( Configuration.PROXY, proxy1 );
            }
        }
        return p;
    }

    @Override
    public void initialize( final URL location, final ResourceBundle resources ) {
        this.rightScrollPane.prefViewportWidthProperty().bind( this.rightVBox.widthProperty() );
        SortedSet<Hoster> values = Hosters.sortedValues();
        AtomicInteger cnt = new AtomicInteger();
        for ( Hoster hoster : values ) {
            int current = cnt.getAndIncrement();
            String hostname = hoster.getName();
            this.rightGridPane.add( new Text( hostname ), 0, current );
            String hosturl = hoster.getBaseUrl().getHost().substring( 4 );
            this.rightGridPane.add( new Text( hosturl ), 1, current );
        }
    }

}
