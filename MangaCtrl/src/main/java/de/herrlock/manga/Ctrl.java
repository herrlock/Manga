package de.herrlock.manga;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import de.herrlock.javafx.AbstractApplication;
import de.herrlock.javafx.scene.SceneContainer;

public final class Ctrl extends AbstractApplication {

    public static void main( String[] args ) {
        Application.launch( args );
    }

    @Override
    public void start( Stage stage ) {
        this.setScene( new CtrlScene() );
        super.start( stage );
    }
}

final class CtrlScene extends SceneContainer {

    public static final ResourceBundle i18n = ResourceBundle.getBundle( "de.herrlock.manga.ctrl" );

    CtrlScene() {
        try {
            URL location = CtrlScene.class.getResource( "CtrlScene.xml" );
            FXMLLoader fxmlLoader = new FXMLLoader( location, i18n );
            Parent root = ( Parent ) fxmlLoader.load();
            this.setScene( new Scene( root ) );
        } catch ( IOException ex ) {
            throw new RuntimeException( ex );
        }

    }

    @Override
    public String getTitle() {
        return "Please select";
    }
}
