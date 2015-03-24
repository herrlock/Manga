package de.herrlock.manga.ui.main;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import de.herrlock.javafx.scene.NodeContainer;

class Center extends NodeContainer {
    @Override
    protected Node createNode() {
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
            final Label lblUrl = new Label( MDGuiStage.I18N.getString( lblPre + "url" ) );
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
            final Label lblPattern = new Label( MDGuiStage.I18N.getString( lblPre + "pattern" ) );
            gridPane.add( lblPattern, 0, y );

            final TextField tfPattern = new TextField();
            tfPattern.setPromptText( "1-10;15;17" );
            gridPane.add( tfPattern, 1, y );
            y++ ;
        }
        {
            final Label lblProxyAddress = new Label( MDGuiStage.I18N.getString( lblPre + "proxyaddress" ) );
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
}