package de.herrlock.manga.ui;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import de.herrlock.javafx.scene.NodeContainer;

class Bottom extends NodeContainer {
    private static final EventHandler<ActionEvent> DO_NOTHING_HANDLER = new EventHandler<ActionEvent>() {
        @Override
        public void handle( ActionEvent arg0 ) {
            System.out.println( "Action not implemented" );
        }
    };

    @Override
    protected Node createNode() {
        String btnPre = "bottom.buttons.";
        Button btnDownload = new Button( MDGuiStage.I18N.getString( btnPre + "download" ) );
        Button btnHTML = new Button( MDGuiStage.I18N.getString( btnPre + "html" ) );
        Button btnExit = new Button( MDGuiStage.I18N.getString( btnPre + "exit" ) );

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
}