package de.herrlock.manga.ui;

import javafx.application.Application;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import de.herrlock.javafx.AbstractApplication;
import de.herrlock.javafx.scene.AbstractScene;

public class MDGui extends AbstractApplication {

    public static void main( String[] args ) {
        Application.launch( args );
    }

    @Override
    public void start( Stage stage ) {
        this.scene = new MDGuiStage();
        super.start( stage );
    }
}

class MDGuiStage extends AbstractScene {
    public MDGuiStage() {
        BorderPane parent = new BorderPane();
        parent.setTop( getTop() );
        parent.setCenter( getCenter() );
        parent.setBottom( getBottom() );

        // set colors, for building tests
        setColor( parent.getTop(), "f99" );
        setColor( parent.getRight(), "ff9" );
        setColor( parent.getBottom(), "9f9" );
        setColor( parent.getLeft(), "99f" );
        setColor( parent.getCenter(), "ccc" );
        //

        this.setScene( new Scene( parent ) );
    }

    private static void setColor( Node node, String color ) {
        if ( node != null ) {
            node.setStyle( "-fx-background-color: #" + color + ";" );
        }
    }

    private Node getTop() {
        Text text = new Text( "someTitle" );
        text.setFont( new Font( 20 ) );
        return text;
    }

    private Node getCenter() {
        Label l1 = new Label( "someLabel" );
        TextField t1 = new TextField( "someTextField" );

        GridPane gp = new GridPane();
        gp.setHgap( 16 );
        gp.setVgap( 8 );
        gp.add( l1, 0, 0 );
        gp.add( t1, 1, 0 );

        return gp;
    }

    private Node getBottom() {
        Button b1 = new Button( "b1" ), b2 = new Button( "b2" ), b3 = new Button( "b3" );

        HBox hbox = new HBox( 8 );
        hbox.getChildren().addAll( b1, b2, b3 );

        AnchorPane pane = new AnchorPane();
        pane.getChildren().addAll( hbox );
        AnchorPane.setBottomAnchor( hbox, 8.0 );
        AnchorPane.setRightAnchor( hbox, 5.0 );
        return pane;
    }

    @Override
    public String getTitle() {
        return "MangaDownloader";
    }
}
