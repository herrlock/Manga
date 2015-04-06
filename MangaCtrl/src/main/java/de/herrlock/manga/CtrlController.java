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

        new Thread( new ExecCtrlHandlerTask( exec ) ).start();
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
