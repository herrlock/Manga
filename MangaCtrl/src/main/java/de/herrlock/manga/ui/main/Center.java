package de.herrlock.manga.ui.main;

import de.herrlock.javafx.scene.NodeContainer;
import javafx.beans.property.StringProperty;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;

final class Center extends NodeContainer {
    @Override
    protected Node createNode() {
        final GridPane gridPane = new GridPane();
        {
            final Label lblTop = new Label();
            lblTop.setPrefWidth( 200 );
            lblTop.setVisible( false );
            gridPane.add( lblTop, 0, 0 );

            final TextField tfTop = new TextField();
            tfTop.setPrefColumnCount( 50 );
            tfTop.setVisible( false );
            gridPane.add( tfTop, 1, 0 );
        }

        String[] keys = {
            "url", "pattern", "proxyaddress", "jdhome"
        };
        String[] prompttexts = {
            "http://www.example.org/manga/manganame", "1-10;15;17", "http://www.example.org:8080", "C:/Program Files/JDownloader/"
        };
        StringProperty[] props = {
            MDGuiController.urlProperty, MDGuiController.patternProperty, MDGuiController.proxyProperty,
            MDGuiController.jdhomeProperty
        };
        for ( int i = 0; i < keys.length; i++ ) {
            String key = keys[i];
            gridPane.add( new XLabel( key ), 0, i );
            gridPane.add( new XTextField( key, prompttexts[i], props[i] ), 1, i );
        }

        {
            final Label lblBtm = new Label();
            lblBtm.setVisible( false );
            gridPane.add( lblBtm, 0, keys.length );

            final TextField tfBtm = new TextField();
            tfBtm.setVisible( false );
            gridPane.add( tfBtm, 1, keys.length );
        }
        gridPane.getStyleClass().addAll( CCN.GRIDPANE, CCN.PADDING_16, CCN.GREY );
        return gridPane;
    }

    private static final class XLabel extends Label {
        private static final String lblPre = "center.label.";

        public XLabel( String i18nKey ) {
            String i18nValue = MDGui.i18n.getString( lblPre + i18nKey );
            setText( i18nValue );
            setTooltip( new Tooltip( i18nValue ) );
        }
    }

    private static final class XTextField extends TextField {
        private static final String txtPre = "center.textfield.";

        public XTextField( String i18nKey, String prompttext, StringProperty prop ) {
            String i18nValue = MDGui.i18n.getString( txtPre + i18nKey );
            setTooltip( new Tooltip( i18nValue ) );
            setPromptText( prompttext );
            prop.bind( textProperty() );
        }
    }
}
