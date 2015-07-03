package de.herrlock.manga;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.text.Text;
import de.herrlock.manga.util.Exec;
import de.herrlock.manga.util.ExecHandlerTask;

public class CtrlController {

    @FXML
    public Text runningText, bottomText;
    @FXML
    public Button btnStartDDL, btnStartPDL, btnShowHosts, btnAddToJD, btnCreateHTML;

    public void clearText() {
        this.bottomText.setText( "" );
    }

    public void showTextDStart() {
        setBottomText( "button.tooltip.startDDL" );
    }
    public void showTextPStart() {
        setBottomText( "button.tooltip.startPDL" );
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

    public void startDDownload() {
        handleBtnClick( Exec.DIALOG_DOWNLOADER );
    }
    public void startPDownload() {
        handleBtnClick( Exec.PLAIN_DOWNLOADER );
    }
    public void showHosts() {
        handleBtnClick( Exec.PRINT_ALL_HOSTER );
    }
    public void exportToJD() {
        handleBtnClick( Exec.ADD_TO_JD_W_FILE );
    }
    public void createHTML() {
        handleBtnClick( Exec.VIEW_PAGE_MAIN );
    }
    public void handleBtnClick( Exec exec ) {
        this.btnStartDDL.setOnAction( null );
        this.btnStartPDL.setOnAction( null );
        this.btnShowHosts.setOnAction( null );
        this.btnAddToJD.setOnAction( null );
        this.btnCreateHTML.setOnAction( null );

        Thread thread = new Thread( new ExecCtrlHandlerTask( exec ) );
        thread.setDaemon( true );
        thread.start();
    }

    class ExecCtrlHandlerTask extends ExecHandlerTask {
        public ExecCtrlHandlerTask( Exec exec ) {
            super( exec );
        }

        @Override
        protected Void call() {
            CtrlController.this.runningText.setVisible( true );
            return super.call();
        }
    }
}
