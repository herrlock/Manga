package de.herrlock.javafx;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import de.herrlock.javafx.scene.SceneContainer;

public abstract class AbstractApplication extends Application {
    private SceneContainer container = null;

    @Override
    public void start( Stage stage ) {
        if ( this.container == null ) {
            throw new IllegalStateException( "need to initialize AbstractApplication.scene before starting this" );
        }
        stage.setTitle( this.container.getTitle() );
        stage.getIcons().addAll( this.container.getIcons() );

        Scene scene = this.container.getScene();
        scene.getStylesheets().addAll( this.container.getStylesheets() );
        stage.setScene( scene );
        stage.show();
    }

    public void setScene( SceneContainer scene ) {
        this.container = scene;
    }
}
