package de.herrlock.manga;

import java.util.ResourceBundle;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
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

    private static final ResourceBundle i18n = ResourceBundle.getBundle( "de.herrlock.manga.ctrl" );

    static final Text runningText = new Text( "Running" );
    static {
        runningText.setVisible( false );
    }
    static final Text bottomText = new Text();

    CtrlScene() {
        BorderPane parent = new BorderPane();
        parent.setPadding( new Insets( 8, 24, 8, 24 ) );
        parent.setTop( runningText );
        parent.setCenter( createCenter() );
        parent.setBottom( bottomText );
        this.setScene( new Scene( parent ) );
    }

    private Node createCenter() {
        EventHandler<MouseEvent> clearText = new SetTextHandler( "" );

        final String buttonTextPrefix = "button.text.";
        final String btnTooltipPrefix = "button.tooltip.";
        Button btnStartDownload = new Button( i18n.getString( buttonTextPrefix + "startDL" ) );
        {
            btnStartDownload.setDefaultButton( true );
            btnStartDownload.setOnAction( new ExecHandler( Exec.DIALOG_DOWNLOADER ) );
            btnStartDownload.setOnMouseEntered( new SetTextHandler( CtrlScene.i18n.getString( btnTooltipPrefix + "startDL" ) ) );
            btnStartDownload.setOnMouseExited( clearText );
        }
        Button btnShowHosts = new Button( i18n.getString( buttonTextPrefix + "showHosts" ) );
        {
            btnShowHosts.setOnAction( new ExecHandler( Exec.PRINT_ALL_HOSTER ) );
            btnShowHosts.setOnMouseEntered( new SetTextHandler( i18n.getString( btnTooltipPrefix + "showHosts" ) ) );
            btnShowHosts.setOnMouseExited( clearText );
        }
        Button btnAddToJD = new Button( i18n.getString( buttonTextPrefix + "addToJD" ) );
        {
            btnAddToJD.setOnAction( new ExecHandler( Exec.ADD_TO_JD ) );
            btnAddToJD.setOnMouseEntered( new SetTextHandler( i18n.getString( btnTooltipPrefix + "addToJD" ) ) );
            btnAddToJD.setOnMouseExited( clearText );
        }
        Button btnCreateHTML = new Button( i18n.getString( buttonTextPrefix + "createHTML" ) );
        {
            btnCreateHTML.setOnAction( new ExecHandler( Exec.VIEW_PAGE_MAIN ) );
            btnCreateHTML.setOnMouseEntered( new SetTextHandler( i18n.getString( btnTooltipPrefix + "createHTML" ) ) );
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

    private static class SetTextHandler implements EventHandler<MouseEvent> {
        private final String textToSet;

        public SetTextHandler( String textToSet ) {
            this.textToSet = textToSet;
        }

        @Override
        public void handle( MouseEvent event ) {
            bottomText.setText( this.textToSet );
        }
    }

    private static class ExecHandler implements EventHandler<ActionEvent> {
        final Exec exec;
        final Task<Void> task;

        public ExecHandler( final Exec exec ) {
            this.exec = exec;
            this.task = new ExecHandlerTask();
            this.task.setOnFailed( new ExceptionHandler() );
        }

        @Override
        public void handle( ActionEvent event ) {
            new Thread( this.task ).start();
        }

        class ExecHandlerTask extends Task<Void> {
            @Override
            protected Void call() {
                runningText.setVisible( true );
                try {
                    ExecHandler.this.exec.execute();
                } finally {
                    Platform.exit();
                }
                return null;
            }
        }

        class ExceptionHandler implements EventHandler<WorkerStateEvent> {
            @Override
            public void handle( WorkerStateEvent t ) {
                Throwable exception = ExecHandler.this.task.getException();
                throw exception instanceof RuntimeException ? ( RuntimeException ) exception : new RuntimeException( exception );
            }
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
