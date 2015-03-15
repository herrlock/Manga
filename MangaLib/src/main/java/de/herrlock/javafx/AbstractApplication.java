package de.herrlock.javafx;

import javafx.application.Application;
import javafx.stage.Stage;
import de.herrlock.javafx.scene.AbstractScene;

public abstract class AbstractApplication extends Application {
    protected AbstractScene scene = null;

    @Override
    public void start( Stage stage ) {
        if ( this.scene == null ) {
            throw new IllegalStateException( "need to initialize AbstractApplication.scene before starting this" );
        }
        stage.setScene( this.scene.getScene() );
        stage.setTitle( this.scene.getTitle() );
        stage.getIcons().addAll( this.scene.getIcons() );
        stage.show();
    }
}
