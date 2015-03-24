package de.herrlock.manga.ui;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import de.herrlock.javafx.AbstractApplication;
import de.herrlock.manga.host.Hoster;
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

    private final ResourceBundle i18n = ResourceBundle.getBundle( "de.herrlock.manga.ui.ui" );

    private static final EventHandler<ActionEvent> DO_NOTHING_HANDLER = new EventHandler<ActionEvent>() {
        @Override
        public void handle( ActionEvent arg0 ) {
            System.out.println( "Action not implemented" );
        }
    };

    public MDGuiStage() {
        BorderPane parent = new BorderPane();
        parent.setTop( getTop() );
        parent.setRight( getRight() );
        parent.setBottom( getBottom() );
        parent.setCenter( getCenter() );
        this.setScene( new Scene( parent ) );
    }

    @Override
    public Collection<String> getStylesheets() {
        return Arrays.asList( "/de/herrlock/manga/ui/style.css" );
    }

    private Node getTop() {
        // TODO: decide title
        Text text = new Text( this.i18n.getString( "top.title" ) );
        text.getStyleClass().add( CCN.TEXT );
        text.setFont( new Font( 30 ) );

        StackPane pane = new StackPane();
        pane.getChildren().addAll( text );
        pane.setAlignment( Pos.TOP_CENTER );
        pane.getStyleClass().add( CCN.RED );
        return pane;
    }

    private Node getRight() {
        Text title = new Text( this.i18n.getString( "right.title" ) );
        title.setFont( new Font( 20 ) );
        title.getStyleClass().add( CCN.TEXT );

        GridPane hostGrid = new GridPane();
        hostGrid.getStyleClass().addAll( CCN.GRIDPANE, CCN.PADDING_8 );
        Hoster[] values = Hoster.sortedValues();
        for ( int y = 0; y < values.length; y++ ) {
            String hostname = values[y].getName();
            hostGrid.add( new Text( hostname ), 0, y );
            String hosturl = values[y].getURL().getHost().substring( 4 );
            hostGrid.add( new Text( hosturl ), 1, y );
        }

        VBox vbox = new VBox( 8 );
        vbox.getStyleClass().addAll( CCN.PADDING_8 );
        vbox.getChildren().addAll( title, hostGrid );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent( vbox );
        scrollPane.prefViewportWidthProperty().bind( vbox.widthProperty() );
        scrollPane.getStyleClass().add( CCN.YELLOW );
        return scrollPane;
    }

    private Node getCenter() {
        // TODO: build center
        int y = 0;
        final GridPane gridPane = new GridPane();
        {
            final Label lblTop = new Label();
            lblTop.setPrefWidth( 150 );
            lblTop.setVisible( false );
            gridPane.add( lblTop, 0, y );

            final TextField tfTop = new TextField();
            tfTop.setPrefColumnCount( 50 );
            tfTop.setVisible( false );
            gridPane.add( tfTop, 1, y );
        }

        String lblPre = "center.label.";
        {
            final Label lblUrl = new Label( this.i18n.getString( lblPre + "url" ) );
            gridPane.add( lblUrl, 0, y );

            final TextField tfUrl = new TextField();
            ChangeListener<String> paramChangeListener = new EmptyListener( tfUrl );
            paramChangeListener.changed( tfUrl.textProperty(), "", tfUrl.getText() );
            tfUrl.textProperty().addListener( paramChangeListener );
            tfUrl.setPromptText( "http://www.example.org/manga/manganame" );
            gridPane.add( tfUrl, 1, y );
            y++ ;
        }
        {
            final Label lblPattern = new Label( this.i18n.getString( lblPre + "pattern" ) );
            gridPane.add( lblPattern, 0, y );

            final TextField tfPattern = new TextField();
            tfPattern.setPromptText( "1-10;15;17" );
            gridPane.add( tfPattern, 1, y );
            y++ ;
        }
        {
            final Label lblProxyAddress = new Label( this.i18n.getString( lblPre + "proxyaddress" ) );
            gridPane.add( lblProxyAddress, 0, y );

            final TextField tfProxyAddress = new TextField();
            tfProxyAddress.setPromptText( "http://www.example.org:8080/proxy" );
            gridPane.add( tfProxyAddress, 1, y );
            y++ ;
        }
        {
            final Label lblBtm = new Label();
            lblBtm.setVisible( false );
            gridPane.add( lblBtm, 0, y );

            final TextField tfBtm = new TextField();
            tfBtm.setVisible( false );
            gridPane.add( tfBtm, 1, y );
        }
        gridPane.getStyleClass().addAll( CCN.GRIDPANE, CCN.PADDING_16, CCN.GREY );
        return gridPane;
    }

    private Node getBottom() {
        String btnPre = "bottom.buttons.";
        Button btnDownload = new Button( this.i18n.getString( btnPre + "download" ) );
        Button btnHTML = new Button( this.i18n.getString( btnPre + "html" ) );
        Button btnExit = new Button( this.i18n.getString( btnPre + "exit" ) );

        btnDownload.setOnAction( DO_NOTHING_HANDLER );
        btnDownload.setDefaultButton( true );
        btnHTML.setOnAction( DO_NOTHING_HANDLER );
        btnExit.setOnAction( DO_NOTHING_HANDLER );
        btnExit.setCancelButton( true );

        HBox hbox = new HBox( 8 );
        hbox.getStyleClass().add( CCN.PADDING_8 );
        hbox.getChildren().addAll( btnDownload, btnHTML, btnExit );

        AnchorPane pane = new AnchorPane();
        pane.getChildren().addAll( hbox );
        AnchorPane.setBottomAnchor( hbox, 0.0 );
        AnchorPane.setRightAnchor( hbox, 0.0 );
        pane.getStyleClass().add( CCN.GREEN );
        return pane;
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
