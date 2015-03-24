package de.herrlock.manga.ui;

import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import de.herrlock.javafx.scene.NodeContainer;

class Top extends NodeContainer {
    @Override
    protected Node createNode() {
        // TODO: decide title
        Text text = new Text( MDGuiStage.I18N.getString( "top.title" ) );
        text.getStyleClass().add( CCN.TEXT );
        text.setFont( new Font( 30 ) );

        StackPane pane = new StackPane();
        pane.getChildren().addAll( text );
        pane.setAlignment( Pos.TOP_CENTER );
        pane.getStyleClass().add( CCN.RED );
        return pane;
    }
}
