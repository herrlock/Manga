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
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
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
        Font font = Font.font( "System", FontWeight.NORMAL, 30 );
        text.setFont( font );
        StackPane pane = new StackPane();
        pane.getChildren().addAll( text );
        StackPane.setAlignment( text, Pos.TOP_CENTER );
        pane.getStyleClass().add( "red" );
        return pane;
    }

    private Node getRight() {
        Text title = new Text( "Hoster" );
        title.setFont( new Font( 20 ) );

        GridPane hostPane = new GridPane();
        hostPane.getStyleClass().addAll( "gridpane", "padding8" );
        Hoster[] values = Hoster.sortedValues();
        for ( int y = 0; y < values.length; y++ ) {
            hostPane.add( new Text( values[y].getName() ), 0, y );
            hostPane.add( new Text( values[y].getURL().getHost().substring( 4 ) ), 1, y );
        }

        VBox vbox = new VBox( 8 );
        hostPane.getStyleClass().addAll( "padding8" );
        vbox.getChildren().addAll( title, hostPane );
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

        GridPane gridpane = new GridPane();
        gridpane.getStyleClass().addAll( "gridpane", "padding16" );

        {
            int y = 0;
            gridpane.add( lblTop, 0, y );
            gridpane.add( tfTop, 1, y++ );
            gridpane.add( lblUrl, 0, y );
            gridpane.add( tfUrl, 1, y++ );
            gridpane.add( lblPattern, 0, y );
            gridpane.add( tfPattern, 1, y++ );

            gridpane.add( lblProxy, 0, y++ );
            gridpane.add( lblProxyHost, 0, y );
            gridpane.add( tfProxyHost, 1, y++ );
            gridpane.add( lblProxyPort, 0, y );
            gridpane.add( tfProxyPort, 1, y++ );

            gridpane.add( lblBtm, 0, y );
            gridpane.add( tfBtm, 1, y++ );
        }

        ColumnConstraints cc1 = new ColumnConstraints();
        cc1.setHgrow( Priority.ALWAYS );
        ColumnConstraints cc2 = new ColumnConstraints();
        cc2.setHgrow( Priority.ALWAYS );

        gridpane.getStyleClass().add( "grey" );
        return gridpane;
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

    private static final EventHandler<ActionEvent> DO_NOTHING_HANDLER = new EventHandler<ActionEvent>() {
        @Override
        public void handle( ActionEvent arg0 ) {
            System.out.println( "Action not implemented" );
        }
    };
}
