package de.herrlock.manga.ui.main;

import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Collection;
import java.util.ResourceBundle;

import de.herrlock.javafx.AbstractApplication;
import de.herrlock.javafx.scene.SceneContainer;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public final class MDGui extends AbstractApplication {
    public static final ResourceBundle i18n = ResourceBundle.getBundle( "de.herrlock.manga.ui.main.ui" );

    public static void main( String... args ) {
        Application.launch( args );
    }

    @Override
    public void start( Stage stage ) {
        this.setScene( new MDGuiStage() );
        super.start( stage );
    }

    static final class MDGuiStage extends SceneContainer {
        MDGuiStage() {
            try {
                URL location = MDGui.class.getResource( "MDGuiScene.xml" );
                Parent root = FXMLLoader.load( location, i18n );
                setScene( new Scene( root ) );
            } catch ( IOException ex ) {
                throw new RuntimeException( ex );
            }
        }

        @Override
        public Collection<String> getStylesheets() {
            return Arrays.asList( "/de/herrlock/manga/ui/main/style.css" );
        }

        @Override
        public String getTitle() {
            return "MangaDownloader";
        }
    }

}
