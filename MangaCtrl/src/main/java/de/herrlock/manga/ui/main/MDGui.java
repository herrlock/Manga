package de.herrlock.manga.ui.main;

import java.util.Arrays;
import java.util.Collection;

import de.herrlock.javafx.AbstractApplication;
import de.herrlock.javafx.scene.SceneContainer;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public final class MDGui extends AbstractApplication {

    public static void main( String... args ) {
        Application.launch( args );
    }

    @Override
    public void start( Stage stage ) {
        this.setScene( new MDGuiStage() );
        super.start( stage );
    }
}

final class MDGuiStage extends SceneContainer {

    public MDGuiStage() {
        BorderPane parent = new BorderPane();
        parent.setTop( new Top().getNode() );
        parent.setRight( new Right().getNode() );
        parent.setBottom( new Bottom().getNode() );
        parent.setCenter( new Center().getNode() );
        this.setScene( new Scene( parent ) );
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
