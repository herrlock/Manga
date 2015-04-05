package de.herrlock.manga.ui.main;

import javafx.beans.value.ChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.layout.GridPane;
import de.herrlock.javafx.scene.NodeContainer;

class Center extends NodeContainer {
    @Override
    protected Node createNode() {
        int y = 0;
        final GridPane gridPane = new GridPane();
        {
            final Label lblTop = new Label();
            lblTop.setPrefWidth( 200 );
            lblTop.setVisible( false );
            gridPane.add( lblTop, 0, y );

            final TextField tfTop = new TextField();
            tfTop.setPrefColumnCount( 50 );
            tfTop.setVisible( false );
            gridPane.add( tfTop, 1, y );
        }

        String lblPre = "center.label.";
        {
            String i18n_url = MDGuiController.i18n.getString( lblPre + "url" );
            final Label lblUrl = new Label( i18n_url );
            lblUrl.setTooltip( new Tooltip( i18n_url ) );
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
            String i18n_pattern = MDGuiController.i18n.getString( lblPre + "pattern" );
            final Label lblPattern = new Label( i18n_pattern );
            lblPattern.setTooltip( new Tooltip( i18n_pattern ) );
            gridPane.add( lblPattern, 0, y );

            final TextField tfPattern = new TextField();
            tfPattern.setPromptText( "1-10;15;17" );
            gridPane.add( tfPattern, 1, y );
            y++ ;
        }
        {
            String i18n_proxyaddress = MDGuiController.i18n.getString( lblPre + "proxyaddress" );
            final Label lblProxyAddress = new Label( i18n_proxyaddress );
            lblProxyAddress.setTooltip( new Tooltip( i18n_proxyaddress ) );
            gridPane.add( lblProxyAddress, 0, y );

            final TextField tfProxyAddress = new TextField();
            tfProxyAddress.setPromptText( "http://www.example.org:8080/proxy" );
            gridPane.add( tfProxyAddress, 1, y );
            y++ ;
        }
        {
            String i18n_jdhome = MDGuiController.i18n.getString( lblPre + "jdhome" );
            final Label lblJDPath = new Label( i18n_jdhome );
            lblJDPath.setTooltip( new Tooltip( i18n_jdhome ) );
            gridPane.add( lblJDPath, 0, y );

            final TextField tfJDPath = new TextField();
            tfJDPath.setPromptText( "C:/Program Files/JDownloader/" );
            gridPane.add( tfJDPath, 1, y );
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
