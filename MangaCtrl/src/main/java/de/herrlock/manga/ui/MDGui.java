package de.herrlock.manga.ui;

import java.util.Arrays;
import java.util.Collection;

import javafx.application.Application;
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
import de.herrlock.javafx.scene.AbstractScene;
import de.herrlock.manga.host.Hoster;

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

class MDGuiStage extends AbstractScene {
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
        Text text = new Text( "someTitle" );
        text.setFont( Font.font( "sans-serif", 30 ) );

        StackPane pane = new StackPane();
        pane.getChildren().addAll( text );
        pane.setAlignment( Pos.TOP_CENTER );
        pane.getStyleClass().add( "red" );
        return pane;
    }

    private Node getRight() {
        Text title = new Text( "Availabile Hoster" );
        title.setFont( Font.font( "sans-serif", 20 ) );

        GridPane hostGrid = new GridPane();
        hostGrid.getStyleClass().addAll( "gridpane", "padding8" );
        Hoster[] values = Hoster.sortedValues();
        for ( int y = 0; y < values.length; y++ ) {
            String hostname = values[y].getName();
            hostGrid.add( new Text( hostname ), 0, y );
            String hosturl = values[y].getURL().getHost().substring( 4 );
            hostGrid.add( new Text( hosturl ), 1, y );
        }

        VBox vbox = new VBox( 8 );
        vbox.getStyleClass().addAll( "padding8" );
        vbox.getChildren().addAll( title, hostGrid );

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent( vbox );
        scrollPane.prefViewportWidthProperty().bind( vbox.widthProperty() );
        scrollPane.getStyleClass().add( "yellow" );
        return scrollPane;
    }

    private Node getCenter() {
        // TODO: build center
        Label lblTop = new Label();
        lblTop.setPrefWidth( 150 );
        lblTop.setVisible( false );
        TextField tfTop = new TextField();
        tfTop.setPrefColumnCount( 50 );
        tfTop.setVisible( false );

        Label lblUrl = new Label( "URL *" );
        TextField tfUrl = new TextField();
        tfUrl.setPromptText( "http://www.example.org/manga/manganame" );

        Label lblPattern = new Label( "Pattern" );
        TextField tfPattern = new TextField();
        tfPattern.setPromptText( "1-10;15;17" );

        Label lblProxy = new Label( "Proxy" );

        Label lblProxyHost = new Label( "Host" );
        TextField tfProxyHost = new TextField();
        tfProxyHost.setPromptText( "http://www.example.org/proxy" );

        Label lblProxyPort = new Label( "Port" );
        TextField tfProxyPort = new TextField();
        tfProxyPort.setPromptText( "8080" );

        Label lblBtm = new Label();
        lblBtm.setVisible( false );
        TextField tfBtm = new TextField();
        tfBtm.setVisible( false );

        final GridPane gridPane = new GridPane();
        gridPane.getStyleClass().addAll( "gridpane", "padding16" );

        {
            int y = 0;
            gridPane.add( lblTop, 0, y );
            gridPane.add( tfTop, 1, y++ );

            gridPane.add( lblUrl, 0, --y );
            gridPane.add( tfUrl, 1, y++ );
            gridPane.add( lblPattern, 0, y );
            gridPane.add( tfPattern, 1, y++ );

            gridPane.add( lblProxy, 0, y++ );
            gridPane.add( lblProxyHost, 0, y );
            gridPane.add( tfProxyHost, 1, y++ );
            gridPane.add( lblProxyPort, 0, y );
            gridPane.add( tfProxyPort, 1, y++ );

            gridPane.add( lblBtm, 0, y );
            gridPane.add( tfBtm, 1, y++ );
        }

        // ScrollPane scrollPane = new ScrollPane();
        // scrollPane.setContent( gridPane );
        // scrollPane.prefViewportWidthProperty().bind( gridPane.widthProperty() );
        // scrollPane.getStyleClass().add( "grey" );
        // return scrollPane;
        gridPane.getStyleClass().add( "grey" );
        return gridPane;
    }

    private Node getBottom() {
        Button btnDownload = new Button( "DL" ), btnHTML = new Button( "HTML" ), btnExit = new Button( "Exit" );

        btnDownload.setOnAction( DO_NOTHING_HANDLER );
        btnDownload.setDefaultButton( true );
        btnHTML.setOnAction( DO_NOTHING_HANDLER );
        btnExit.setOnAction( DO_NOTHING_HANDLER );
        btnExit.setCancelButton( true );

        HBox hbox = new HBox( 8 );
        hbox.getStyleClass().add( "padding8" );
        hbox.getChildren().addAll( btnDownload, btnHTML, btnExit );

        AnchorPane pane = new AnchorPane();
        pane.getChildren().addAll( hbox );
        AnchorPane.setBottomAnchor( hbox, 0.0 );
        AnchorPane.setRightAnchor( hbox, 0.0 );
        pane.getStyleClass().add( "green" );
        return pane;
    }

    @Override
    public String getTitle() {
        return "MangaDownloader";
    }
}
