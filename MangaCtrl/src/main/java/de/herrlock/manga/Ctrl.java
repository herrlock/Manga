package de.herrlock.manga;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import de.herrlock.javafx.AbstractApplication;
import de.herrlock.javafx.scene.SceneContainer;
import de.herrlock.manga.exceptions.MDRuntimeException;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class Ctrl extends AbstractApplication {

    public static final ResourceBundle i18n = ResourceBundle.getBundle( "de.herrlock.manga.Ctrl" );

    public static void main( final String... args ) {
        Application.launch( args );
    }

    @Override
    public void start( final Stage stage ) {
        setScene( new CtrlScene() );
        super.start( stage );
    }

    static final class CtrlScene extends SceneContainer {
        CtrlScene() {
            try {
                URL location = CtrlScene.class.getResource( "CtrlScene.xml" );
                Parent root = FXMLLoader.load( location, i18n );
                setScene( new Scene( root ) );
            } catch ( IOException ex ) {
                throw new MDRuntimeException( ex );
            }
        }

        @Override
        public String getTitle() {
            return i18n.getString( "scene.title" );
        }
    }
}
