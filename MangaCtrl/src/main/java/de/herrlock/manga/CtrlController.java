package de.herrlock.manga;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.text.Text;
import de.herrlock.manga.downloader.DialogDownloader;
import de.herrlock.manga.host.PrintAllHoster;
import de.herrlock.manga.html.ViewPageMain;
import de.herrlock.manga.jd.JDExport;

public class CtrlController {

    @FXML
    Text runningText, bottomText;

    public void clearText() {
        this.bottomText.setText( "" );
    }

    public void showTextStart() {
        CtrlController.this.bottomText.setText( CtrlScene.i18n.getString( "button.tooltip.startDL" ) );
    }

    public void showTextHosts() {
        CtrlController.this.bottomText.setText( CtrlScene.i18n.getString( "button.tooltip.showHosts" ) );
    }

    public void showTextJD() {
        CtrlController.this.bottomText.setText( CtrlScene.i18n.getString( "button.tooltip.addToJD" ) );
    }

    public void showTextHTML() {
        CtrlController.this.bottomText.setText( CtrlScene.i18n.getString( "button.tooltip.createHTML" ) );
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
