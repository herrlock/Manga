package de.herrlock.javafx;

import java.util.concurrent.ExecutionException;

import de.herrlock.manga.exceptions.MyException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

/**
 * 
 * http://stackoverflow.com/a/12718117/3680684
 */
public final class Dialogs {

    public static enum Response {
        NO, YES, CANCEL, OK;
    }

    private static ImageView icon = new ImageView();

    static final class Dialog extends Stage {

        private Response selectedButton = Response.CANCEL;

        private Dialog( final String title, final Stage owner, final Parent parent, final String iconFile ) {
            setTitle( title );
            initStyle( StageStyle.UTILITY );
            initModality( Modality.APPLICATION_MODAL );
            initOwner( owner );
            setResizable( false );
            setScene( new Scene( parent ) );
            icon.setImage( new Image( getClass().getResourceAsStream( iconFile ) ) );
        }

        public void showDialog() {
            super.sizeToScene();
            super.centerOnScreen();
            super.showAndWait();
        }

        public Response getSelectedButton() {
            return this.selectedButton;
        }

        public void setSelectedButton( final Response selectedButton ) {
            this.selectedButton = selectedButton;
        }
    }

    static final class Message extends Text {
        public Message( final String msg ) {
            super( msg );
            setWrappingWidth( 500 );
        }
    }

    private static Dialog _createDialog( final String title, final Stage owner, final Parent parent, final String iconFile ) {
        final Task<Dialog> task = new Task<Dialog>() {
            @Override
            protected Dialog call() throws Exception {
                return new Dialog( title, owner, parent, iconFile );
            }
        };
        Platform.runLater( task );
        try {
            return task.get();
        } catch ( final InterruptedException | ExecutionException ex ) {
            throw new MyException( ex );
        }
    }

    private static Response _showDialog( final Dialog dialog ) {
        Task<Response> task = new Task<Response>() {
            @Override
            protected Response call() throws Exception {
                dialog.showDialog();
                return dialog.getSelectedButton();
            }
        };
        Platform.runLater( task );
        try {
            return task.get();
        } catch ( InterruptedException | ExecutionException ex ) {
            throw new MyException( ex );
        }
    }

    /**
     * 
     * @param owner
     * @param message
     * @param title
     * @param iconFile
     * @return always returns {@link Response#OK}
     */
    public static Response showDialog( final Stage owner, final Node message, final String title, final String iconFile ) {
        final VBox vb = new VBox();
        vb.setPadding( new Insets( 10 ) );
        vb.setSpacing( 10 );
        final Button okButton = new Button( "OK" );
        okButton.setAlignment( Pos.CENTER );
        final BorderPane bp = new BorderPane();
        bp.setCenter( okButton );
        final HBox msg = new HBox();
        msg.setSpacing( 5 );
        msg.getChildren().addAll( icon, message );
        vb.getChildren().addAll( msg, bp );
        final Dialog dialog = _createDialog( title, owner, vb, iconFile );
        okButton.setOnAction( new EventHandler<ActionEvent>() {
            @Override
            public void handle( final ActionEvent e ) {
                dialog.setSelectedButton( Response.OK );
                dialog.close();
            }
        } );
        return _showDialog( dialog );
    }

    public static Response showConfirmDialog( final Stage owner, final Node message, final String title ) {
        final VBox vb = new VBox();
        vb.setPadding( new Insets( 10 ) );
        vb.setSpacing( 10 );
        final Button yesButton = new Button( "Yes" );
        final Button noButton = new Button( "No" );
        BorderPane bp = new BorderPane();
        HBox buttons = new HBox();
        buttons.setAlignment( Pos.CENTER );
        buttons.setSpacing( 10 );
        buttons.getChildren().addAll( yesButton, noButton );
        bp.setCenter( buttons );
        HBox msg = new HBox();
        msg.setSpacing( 5 );
        msg.getChildren().addAll( icon, message );
        vb.getChildren().addAll( msg, bp );
        final Dialog dialog = _createDialog( title, owner, vb, "dialog-confirm.png" );
        yesButton.setOnAction( new EventHandler<ActionEvent>() {
            @Override
            public void handle( final ActionEvent e ) {
                dialog.setSelectedButton( Response.YES );
                dialog.close();
            }
        } );
        noButton.setOnAction( new EventHandler<ActionEvent>() {
            @Override
            public void handle( final ActionEvent e ) {
                dialog.setSelectedButton( Response.NO );
                dialog.close();
            }
        } );
        return _showDialog( dialog );
    }

    public static Response showConfirmDialog( final Stage owner, final String message, final String title ) {
        return showConfirmDialog( owner, new Message( message ), title );
    }

    public static void showMessageDialog( final Stage owner, final String message, final String title ) {
        showMessageDialog( owner, new Message( message ), title );
    }

    public static void showMessageDialog( final Stage owner, final Node message, final String title ) {
        showDialog( owner, message, title, "dialog-information.png" );
    }

    public static void showWarningDialog( final Stage owner, final String message, final String title ) {
        showWarningDialog( owner, new Message( message ), title );
    }

    public static void showWarningDialog( final Stage owner, final Node message, final String title ) {
        showDialog( owner, message, title, "dialog-warning.png" );
    }

    public static void showErrorDialog( final Stage owner, final String message, final String title ) {
        showErrorDialog( owner, new Message( message ), title );
    }

    public static void showErrorDialog( final Stage owner, final Node message, final String title ) {
        showDialog( owner, message, title, "dialog-error.png" );
    }

    private Dialogs() {
        // not used
    }

}
