package de.herrlock.manga;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import de.herrlock.manga.downloader.DialogDownloader;
import de.herrlock.manga.host.PrintAllHoster;
import de.herrlock.manga.html.ViewPageMain;
import de.herrlock.manga.jd.JDExport;

public class CtrlController {

    @FXML
    public Text runningText, bottomText;
    @FXML
    public Button btnStartDL, btnShowHosts, btnAddToJD, btnCreateHTML;

    public void clearText() {
        this.bottomText.setText( "" );
    }

    public void showTextStart() {
        setBottomText( "button.tooltip.startDL" );
    }
    public void showTextHosts() {
        setBottomText( "button.tooltip.showHosts" );
    }
    public void showTextJD() {
        setBottomText( "button.tooltip.addToJD" );
    }
    public void showTextHTML() {
        setBottomText( "button.tooltip.createHTML" );
    }
    private void setBottomText( String i18nKey ) {
        CtrlController.this.bottomText.setText( Ctrl.i18n.getString( i18nKey ) );
    }

    public void startDownload() {
        handleBtnClick( Exec.DIALOG_DOWNLOADER );
    }
    public void showHosts() {
        handleBtnClick( Exec.PRINT_ALL_HOSTER );
    }
    public void exportToJD() {
        handleBtnClick( Exec.ADD_TO_JD );
    }
    public void createHTML() {
        handleBtnClick( Exec.VIEW_PAGE_MAIN );
    }
    public void handleBtnClick( Exec exec ) {
        this.btnStartDL.setOnAction( null );
        this.btnShowHosts.setOnAction( null );
        this.btnAddToJD.setOnAction( null );
        this.btnCreateHTML.setOnAction( null );
        new Thread( new ExecHandlerTask( exec ) ).start();
    }

    class ExecHandlerTask extends Task<Void> {
        private final Exec exec;

        public ExecHandlerTask( Exec exec ) {
            this.exec = exec;
            setOnFailed( new ExceptionHandler() );
        }

        @Override
        protected Void call() {
            CtrlController.this.runningText.setVisible( true );
            try {
                this.exec.execute();
            } finally {
                Platform.exit();
            }
            return null;
        }

        class ExceptionHandler implements EventHandler<WorkerStateEvent> {
            @Override
            public void handle( WorkerStateEvent t ) {
                Throwable exception = ExecHandlerTask.this.getException();
                throw exception instanceof RuntimeException ? ( RuntimeException ) exception : new RuntimeException( exception );
            }
        }
    }
}

enum Exec {
    DIALOG_DOWNLOADER {
        @Override
        public void execute() {
            DialogDownloader.execute();
        }
    },
    ADD_TO_JD {
        @Override
        public void execute() {
            JDExport.execute();
        }
    },
    PRINT_ALL_HOSTER {
        @Override
        public void execute() {
            PrintAllHoster.execute();
        }
    },
    VIEW_PAGE_MAIN {
        @Override
        public void execute() {
            ViewPageMain.execute();
        }
    };

    public abstract void execute();
}
