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
    final Text runningText = new Text();
    final Text bottomText = new Text();

    CtrlScene() {
        BorderPane parent = new BorderPane();
        parent.setPadding( new Insets( 8, 24, 8, 24 ) );
        parent.setTop( this.runningText );
        parent.setCenter( createCenter() );
        parent.setBottom( this.bottomText );
        this.setScene( new Scene( parent ) );
    }
    private Node createCenter() {
        EventHandler<MouseEvent> clearText = new EventHandler<MouseEvent>() {
            @Override
            public void handle( MouseEvent event ) {
                CtrlScene.this.bottomText.setText( "" );
            }
        };

        final String buttonTextPrefix = "button.text.";
        final String buttonTooltipPrefix = "button.tooltip.";
        Button btnStartDownload = new Button( this.i18n.getString( buttonTextPrefix + "startDL" ) );
        {
            btnStartDownload.setDefaultButton( true );
            btnStartDownload.setOnAction( new TaskHandler( new MDTask( Exec.DIALOG_DOWNLOADER ) ) );
            btnStartDownload.setOnMouseEntered( new SetTextHandler( this.i18n.getString( buttonTooltipPrefix + "startDL" ) ) );
            btnStartDownload.setOnMouseExited( clearText );
        }
        Button stnShowHosts = new Button( this.i18n.getString( buttonTextPrefix + "showHosts" ) );
        {
            stnShowHosts.setOnAction( new TaskHandler( new MDTask( Exec.PRINT_ALL_HOSTER ) ) );
            stnShowHosts.setOnMouseEntered( new SetTextHandler( this.i18n.getString( buttonTooltipPrefix + "showHosts" ) ) );
            stnShowHosts.setOnMouseExited( clearText );
        }
        Button btnCreateHTML = new Button( this.i18n.getString( buttonTextPrefix + "createHTML" ) );
        {
            btnCreateHTML.setOnAction( new TaskHandler( new MDTask( Exec.VIEW_PAGE_MAIN ) ) );
            btnCreateHTML.setOnMouseEntered( new SetTextHandler( this.i18n.getString( buttonTooltipPrefix + "createHTML" ) ) );
            btnCreateHTML.setOnMouseExited( clearText );
        }
        HBox hbox = new HBox( 8 );
        hbox.setPadding( new Insets( 8 ) );
        hbox.getChildren().addAll( btnStartDownload, stnShowHosts, btnCreateHTML );
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

    class MDTask extends Task<Void> {
        private final Exec exec;

        public MDTask( Exec exec ) {
            this.exec = exec;
        }

        @Override
        protected Void call() {
            CtrlScene.this.runningText.setText( "Working, please wait" );
            this.exec.execute();
            CtrlScene.this.runningText.setText( "" );
            Platform.exit();
            return null;
        }
    }
}

abstract class Exec {
    public static final Exec DIALOG_DOWNLOADER = new Exec() {
        @Override
        public void execute() {
            DialogDownloader.execute();
        }
    }, PRINT_ALL_HOSTER = new Exec() {
        @Override
        public void execute() {
            PrintAllHoster.execute();
        }
    }, VIEW_PAGE_MAIN = new Exec() {
        @Override
        public void execute() {
            ViewPageMain.execute();
        }
    };

    public abstract void execute();
}
