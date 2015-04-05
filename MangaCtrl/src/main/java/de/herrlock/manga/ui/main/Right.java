package de.herrlock.manga.ui.main;

import javafx.scene.Node;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import de.herrlock.javafx.scene.NodeContainer;
import de.herrlock.manga.host.Hoster;

class Right extends NodeContainer {
    @Override
    protected Node createNode() {
        Text title = new Text( MDGuiController.i18n.getString( "right.title" ) );
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
}
