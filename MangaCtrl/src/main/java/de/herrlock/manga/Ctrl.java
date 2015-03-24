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
import de.herrlock.javafx.AbstractApplication;
import de.herrlock.javafx.scene.SceneContainer;
import de.herrlock.manga.downloader.DialogDownloader;
import de.herrlock.manga.host.PrintAllHoster;
import de.herrlock.manga.html.ViewPageMain;
import de.herrlock.manga.jd.JDExport;

public final class Ctrl extends AbstractApplication {

    public static void main( String[] args ) {
        Application.launch( args );
    }

    @Override
    public void start( Stage stage ) {
        this.setScene( new CtrlScene() );
        super.start( stage );
    }
}

final class CtrlScene extends SceneContainer {

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
            btnStartDownload.setOnAction( new TaskHandler( Exec.DIALOG_DOWNLOADER ) );
            btnStartDownload.setOnMouseEntered( new SetTextHandler( this.i18n.getString( buttonTooltipPrefix + "startDL" ) ) );
            btnStartDownload.setOnMouseExited( clearText );
        }
        Button btnShowHosts = new Button( this.i18n.getString( buttonTextPrefix + "showHosts" ) );
        {
            btnShowHosts.setOnAction( new TaskHandler( Exec.PRINT_ALL_HOSTER ) );
            btnShowHosts.setOnMouseEntered( new SetTextHandler( this.i18n.getString( buttonTooltipPrefix + "showHosts" ) ) );
            btnShowHosts.setOnMouseExited( clearText );
        }
        Button btnAddToJD = new Button( this.i18n.getString( buttonTextPrefix + "addToJD" ) );
        {
            btnAddToJD.setOnAction( new TaskHandler( Exec.ADD_TO_JD ) );
            btnAddToJD.setOnMouseEntered( new SetTextHandler( this.i18n.getString( buttonTooltipPrefix + "addToJD" ) ) );
            btnAddToJD.setOnMouseExited( clearText );
        }
        Button btnCreateHTML = new Button( this.i18n.getString( buttonTextPrefix + "createHTML" ) );
        {
            btnCreateHTML.setOnAction( new TaskHandler( Exec.VIEW_PAGE_MAIN ) );
            btnCreateHTML.setOnMouseEntered( new SetTextHandler( this.i18n.getString( buttonTooltipPrefix + "createHTML" ) ) );
            btnCreateHTML.setOnMouseExited( clearText );
        }
        HBox hbox = new HBox( 8 );
        hbox.setPadding( new Insets( 8 ) );
        hbox.getChildren().addAll( btnStartDownload, btnAddToJD, btnShowHosts, btnCreateHTML );
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

        public TaskHandler( Exec exec ) {
            this( new MDTask( exec ) );
        }

        public TaskHandler( Task<?> task ) {
            this.task = task;
        }

        @Override
        public void handle( ActionEvent event ) {
            Thread thread = new Thread( this.task );
            Platform.exit();
            thread.start();
        }
    }

    private static class MDTask extends Task<Void> {
        private final Exec exec;

        public MDTask( Exec exec ) {
            this.exec = exec;
        }

        @Override
        protected Void call() {
            try {
                this.exec.execute();
            } catch ( RuntimeException ex ) {
                ex.printStackTrace();
                throw ex;
            } catch ( Exception ex ) {
                ex.printStackTrace();
                throw new RuntimeException( ex );
            }
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
    }, ADD_TO_JD = new Exec() {
        @Override
        public void execute() {
            JDExport.execute();
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
