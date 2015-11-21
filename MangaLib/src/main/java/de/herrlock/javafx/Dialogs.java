package de.herrlock.javafx;

import java.io.IOException;
import java.io.InputStream;
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

    static final class Dialog extends Stage {

        private Response selectedButton = Response.CANCEL;

        public Dialog( final String title, final Stage owner, final Node message, final Parent buttons, final String iconFile ) {
            setTitle( title );
            initStyle( StageStyle.UTILITY );
            initModality( Modality.APPLICATION_MODAL );
            initOwner( owner );
            setResizable( false );
            final HBox topContent = new HBox( 5 );
            try ( final InputStream in = Dialogs.class.getResourceAsStream( iconFile ) ) {
                if ( in != null ) {
                    final Image image = new Image( in, 100, -1, true, true );
                    final ImageView imageView = new ImageView( image );
                    topContent.getChildren().add( imageView );
                }
            } catch ( IOException ex ) {
                throw new MyException( ex );
            }
            topContent.getChildren().add( message );
            final VBox vbox = new VBox( 10 );
            vbox.setPadding( new Insets( 10 ) );
            vbox.getChildren().addAll( topContent, buttons );
            setScene( new Scene( vbox ) );
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

    public static class DialogHider implements Runnable {
        private final Dialog dialog;

        public DialogHider( Dialog dialog ) {
            this.dialog = dialog;
        }

        @Override
        public void run() {
            this.dialog.close();
        }

        public void execute() {
            Platform.runLater( this );
        }
    }

    private static Dialog _createDialog( final String title, final Stage owner, final Node parent, final Parent buttons,
        final String iconFile ) {
        final Task<Dialog> task = new Task<Dialog>() {
            @Override
            protected Dialog call() {
                return new Dialog( title, owner, parent, buttons, iconFile );
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
            protected Response call() {
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
        final Button okButton = new Button( "OK" );
        okButton.setAlignment( Pos.CENTER );
        final BorderPane bp = new BorderPane();
        bp.setCenter( okButton );
        final Dialog dialog = _createDialog( title, owner, message, bp, iconFile );
        okButton.setOnAction( new EventHandler<ActionEvent>() {
            @Override
            public void handle( final ActionEvent e ) {
                dialog.setSelectedButton( Response.OK );
                dialog.close();
            }
        } );
        return _showDialog( dialog );
    }

    public static DialogHider showLoadingDialog( final Stage owner, final Node message, final String title,
        final String iconFile ) {
        final Dialog dialog = _createDialog( title, owner, message, new VBox(), iconFile );
        dialog.initModality( Modality.NONE );
        _showDialog( dialog );
        return new DialogHider( dialog );
    }

    public static DialogHider showLoadingDialog( final Stage owner, final String message, final String title,
        final String iconFile ) {
        return showLoadingDialog( owner, new Message( message ), title, iconFile );
    }

    public static DialogHider showLoadingDialog( final Stage owner, final Node message, final String title ) {
        return showLoadingDialog( owner, message, title, "glumanda.gif" );
    }

    public static DialogHider showLoadingDialog( final Stage owner, final String message, final String title ) {
        return showLoadingDialog( owner, new Message( message ), title );
    }

    public static Response showConfirmDialog( final Stage owner, final Node message, final String title ) {
        final Button yesButton = new Button( "Yes" );
        final Button noButton = new Button( "No" );
        HBox buttons = new HBox( 10 );
        buttons.setAlignment( Pos.CENTER );
        buttons.getChildren().addAll( yesButton, noButton );
        final Dialog dialog = _createDialog( title, owner, message, buttons, "dialog-confirm.png" );
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
