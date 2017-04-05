package de.herrlock.manga;

import de.herrlock.javafx.handler.Exec;
import de.herrlock.javafx.handler.ExecHandlerTask;
import de.herrlock.manga.util.Execs;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

public final class CtrlController {

    @FXML
    public Text runningText, bottomText;

    @FXML
    public Button btnStartDDL, btnStartPDL, btnStartServer, btnShowHosts, btnCreateHTML;

    public void clearText() {
        this.bottomText.setText( "" );
    }

    public void showTextDStart() {
        setBottomText( "button.tooltip.startDDL" );
    }

    public void showTextPStart() {
        setBottomText( "button.tooltip.startPDL" );
    }

    public void showTextServerStart() {
        setBottomText( "button.tooltip.startServer" );
    }

    public void showTextHosts() {
        setBottomText( "button.tooltip.showHosts" );
    }

    public void showTextHTML() {
        setBottomText( "button.tooltip.createHTML" );
    }

    private void setBottomText( final String i18nKey ) {
        this.bottomText.setText( Ctrl.I18N.getString( i18nKey ) );
    }

    public void startDDownload() {
        handleBtnClick( Execs.DIALOG_DOWNLOADER );
    }

    public void startPDownload() {
        handleBtnClick( Execs.SETTINGS_FILE_DOWNLOADER );
    }

    public void startServer() {
        handleBtnClick( Execs.START_SERVER, false );
    }

    public void showHosts() {
        Platform.runLater( new ShowHosterTask() );
    }

    public void createHtml() {
        handleBtnClick( Execs.VIEW_PAGE_MAIN );
    }

    public void handleBtnClick( final Exec exec ) {
        handleBtnClick( exec, true );
    }

    public void handleBtnClick( final Exec exec, final boolean daemon ) {
        this.btnStartDDL.setOnAction( null );
        this.btnStartPDL.setOnAction( null );
        this.btnStartServer.setOnAction( null );
        this.btnShowHosts.setOnAction( null );
        this.btnCreateHTML.setOnAction( null );

        Thread thread = new Thread( new ExecCtrlHandlerTask( exec ) );
        thread.setDaemon( daemon );
        thread.start();
    }

    final class ExecCtrlHandlerTask extends ExecHandlerTask {
        public ExecCtrlHandlerTask( final Exec exec ) {
            super( exec );
        }

        @Override
        protected Void call() {
            CtrlController.this.runningText.setVisible( true );
            return super.call();
        }
    }

    static final class ShowHosterTask extends Task<Void> {
        @Override
        protected Void call() {
            HosterDialog.showHosterDialog();
            return null;
        }
    }
}
