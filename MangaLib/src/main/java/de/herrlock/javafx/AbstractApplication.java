package de.herrlock.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import de.herrlock.javafx.scene.AbstractScene;

public abstract class AbstractApplication extends Application {
    private AbstractScene abstractScene = null;

    @Override
    public void start( Stage stage ) {
        if ( this.abstractScene == null ) {
            throw new IllegalStateException( "need to initialize AbstractApplication.scene before starting this" );
        }
        Scene scene = this.abstractScene.getScene();
        scene.getStylesheets().addAll( this.abstractScene.getStylesheets() );
        stage.setScene( scene );
        stage.setTitle( this.abstractScene.getTitle() );
        stage.getIcons().addAll( this.abstractScene.getIcons() );
        stage.show();
    }

    public void setScene( AbstractScene scene ) {
        this.abstractScene = scene;
    }
}
