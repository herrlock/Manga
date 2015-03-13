package de.herrlock.manga;

import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import de.herrlock.javafx.scene.AbstractScene;
import de.herrlock.manga.downloader.DialogDownloader;
import de.herrlock.manga.host.PrintAllHoster;
import de.herrlock.manga.html.ViewPageMain;

public final class Ctrl extends Application {

    public static void main( String[] args ) {
        Application.launch( args );
    }

    @Override
    public void start( Stage stage ) {
        AbstractScene scene = new CtrlScene();
        stage.setScene( scene.getScene() );
        stage.setTitle( scene.getTitle() );
        stage.getIcons().addAll( scene.getIcons() );
        stage.show();
    }
}

final class CtrlScene extends AbstractScene {

    private final ResourceBundle i18n = ResourceBundle.getBundle( "de.herrlock.manga.ctrl" );
    final Text bottomText = new Text();

    CtrlScene() {
        BorderPane parent = new BorderPane();
        parent.setPadding( new Insets( 8, 24, 8, 24 ) );
        parent.setCenter( createCenter() );
        parent.setBottom( this.bottomText );
        this.setScene( new Scene( parent ) );
    }

    private Node createCenter() {
        EventHandler<MouseEvent> cte = new EventHandler<MouseEvent>() {
            @Override
            public void handle( MouseEvent event ) {
                CtrlScene.this.bottomText.setText( "" );
            }
        };

        final String buttonTextPrefix = "button.text.";
        final String buttonTooltipPrefix = "button.tooltip.";
        Button startDL = new Button( this.i18n.getString( buttonTextPrefix + "startDL" ) );
        {
            startDL.setDefaultButton( true );
            startDL.setOnAction( new TaskHandler( StaticTasks.START_DOWNLOAD ) );
            startDL.setOnMouseEntered( new SetTextHandler( this.i18n.getString( buttonTooltipPrefix + "startDL" ) ) );
            startDL.setOnMouseExited( cte );
        }
        Button showHosts = new Button( this.i18n.getString( buttonTextPrefix + "showHosts" ) );
        {
            showHosts.setOnAction( new TaskHandler( StaticTasks.SHOW_HOSTER ) );
            showHosts.setOnMouseEntered( new SetTextHandler( this.i18n.getString( buttonTooltipPrefix + "showHosts" ) ) );
            showHosts.setOnMouseExited( cte );
        }
        Button createHTML = new Button( this.i18n.getString( buttonTextPrefix + "createHTML" ) );
        {
            createHTML.setOnAction( new TaskHandler( StaticTasks.CREATE_HTML ) );
            createHTML.setOnMouseEntered( new SetTextHandler( this.i18n.getString( buttonTooltipPrefix + "createHTML" ) ) );
            createHTML.setOnMouseExited( cte );
        }
        HBox hbox = new HBox( 8 );
        hbox.setPadding( new Insets( 8 ) );
        hbox.getChildren().addAll( startDL, showHosts, createHTML );
        return hbox;
    }

    @Override
    public String getTitle() {
        return "Please select";
    }

    private class SetTextHandler implements EventHandler<MouseEvent> {
        private final String textToSet;

        public SetTextHandler( String textToSet ) {
            this.textToSet = textToSet;
        }

        @Override
        public void handle( MouseEvent event ) {
            CtrlScene.this.bottomText.setText( this.textToSet );
        }
    }

    private static class TaskHandler implements EventHandler<ActionEvent> {
        private final Task<?> task;

        public TaskHandler( Task<?> task ) {
            this.task = task;
        }
        @Override
        public void handle( ActionEvent event ) {
            new Thread( this.task ).start();
        }
    }
}

final class StaticTasks {

    public static final Task<Void> START_DOWNLOAD = new Task<Void>() {
        @Override
        protected Void call() {
            DialogDownloader.execute();
            Platform.exit();
            return null;
        }
    };
    public static final Task<Void> SHOW_HOSTER = new Task<Void>() {
        @Override
        protected Void call() {
            PrintAllHoster.execute();
            Platform.exit();
            return null;
        }
    };
    public static final Task<Void> CREATE_HTML = new Task<Void>() {
        @Override
        protected Void call() {
            ViewPageMain.execute();
            Platform.exit();
            return null;
        }
    };

    private StaticTasks() {
        // not used
    }
}
