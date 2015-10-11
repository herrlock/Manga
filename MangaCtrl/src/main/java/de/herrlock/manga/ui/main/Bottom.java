package de.herrlock.manga.ui.main;

import de.herrlock.javafx.scene.NodeContainer;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

final class Bottom extends NodeContainer {
    @Override
    protected Node createNode() {
        String btnPre = "bottom.buttons.";

        Button btnDownload = new Button( MDGui.i18n.getString( btnPre + "download" ) );
        btnDownload.setOnAction( MDGuiController.START_DOWNLOAD );
        btnDownload.setDefaultButton( true );

        Button btnJDExport = new Button( MDGui.i18n.getString( btnPre + "jdexport" ) );
        btnJDExport.setOnAction( MDGuiController.EXPORT_TO_JD );

        Button btnHTML = new Button( MDGui.i18n.getString( btnPre + "html" ) );
        btnHTML.setOnAction( MDGuiController.CREATE_HTML );

        Button btnExit = new Button( MDGui.i18n.getString( btnPre + "exit" ) );
        btnExit.setOnAction( MDGuiController.EXIT_GUI );
        btnExit.setCancelButton( true );

        HBox hbox = new HBox( 8 );
        hbox.getStyleClass().add( CCN.PADDING_8 );
        hbox.getChildren().addAll( btnDownload, btnJDExport, btnHTML, btnExit );

        AnchorPane pane = new AnchorPane();
        pane.getChildren().addAll( hbox );
        AnchorPane.setBottomAnchor( hbox, 0.0 );
        AnchorPane.setRightAnchor( hbox, 0.0 );
        pane.getStyleClass().add( CCN.GREEN );
        return pane;
    }
}
