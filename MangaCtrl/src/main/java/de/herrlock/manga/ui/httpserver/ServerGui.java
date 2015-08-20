package de.herrlock.manga.ui.httpserver;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import de.herrlock.javafx.AbstractApplication;
import de.herrlock.javafx.scene.SceneContainer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

/**
 * @author HerrLock
 */
public class ServerGui extends AbstractApplication {

    private static final Logger logger = LogManager.getLogger();

    public static void main( String... args ) {
        Application.launch( args );
    }

    @Override
    public void start( Stage stage ) {
        logger.entry();
        setScene( new ServerScene() );
        super.start( stage );
    }

    @Override
    public void stop() {
        logger.entry();
        System.out.println( "" );
    }

    static class ServerScene extends SceneContainer {
        ServerScene() {
            try {
                URL location = ServerScene.class.getResource( "ServerScene.xml" );
                Parent root = FXMLLoader.load( location, ServerController.i18n );
                setScene( new Scene( root ) );
            } catch ( IOException ex ) {
                throw new RuntimeException( ex );
            }
        }

        @Override
        public String getTitle() {
            return ServerController.i18n.getString( "scene.title" );
        }

        @Override
        public Collection<Image> getIcons() {
            Image img = new Image( "/de/herrlock/manga/ui/img/server.png" );
            return Arrays.asList( img );
        }
    }

}
