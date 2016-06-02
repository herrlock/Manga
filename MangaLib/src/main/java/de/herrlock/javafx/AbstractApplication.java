package de.herrlock.javafx;

import de.herrlock.javafx.annotation.Conf;
import de.herrlock.javafx.scene.SceneContainer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

/**
 * A superclass for Applications. Must be used together with {@link SceneContainer}.
 * 
 * @author HerrLock
 * @see SceneContainer
 */
public abstract class AbstractApplication extends Application {
    private SceneContainer container;

    /**
     * search for <code>@Conf</code>-annotation and configurate the environment
     */
    protected AbstractApplication() {
        Conf conf = this.getClass().getAnnotation( Conf.class );
        if ( conf != null && conf.enableBiDirectional() ) {
            // enable bidirectonal binding if set
            System.setProperty( "javafx.fxml.FXMLLoader.enableBidirectionalBinding", "true" );
        }
    }

    /**
     * Start the application. Requires a previous called {@linkplain #setScene(SceneContainer)} to get the
     * {@linkplain SceneContainer#getTitle() title}, the {@linkplain SceneContainer#getIcons() icons}, the
     * {@linkplain SceneContainer#getStylesheets() stylesheets} and the {@linkplain SceneContainer#getScene() actual scene} from
     */
    @Override
    public void start( final Stage stage ) {
        if ( this.container == null ) {
            throw new IllegalStateException( "need to initialize AbstractApplication.container before starting this" );
        }
        stage.setTitle( this.container.getTitle() );
        stage.getIcons().addAll( this.container.getIcons() );

        Scene scene = this.container.getScene();
        scene.getStylesheets().addAll( this.container.getStylesheets() );
        stage.setScene( scene );
        stage.show();
    }

    /**
     * Sets the SceneContainer to use, the contained Scene is shown by this Application
     * 
     * @param container
     *            the {@link SceneContainer} to use
     */
    public void setScene( final SceneContainer container ) {
        this.container = container;
    }
}
