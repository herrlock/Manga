package de.herrlock.javafx;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.ExecutionException;

import de.herrlock.manga.exceptions.MDRuntimeException;
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

    /**
     * possible Responses for the dialogs
     */
    @SuppressWarnings( "javadoc" )
    public static enum Response {
        YES, NO, CANCEL, OK;
    }

    /**
     * A JavaFX-styled Dialog
     */
    static final class Dialog extends Stage {

        private Response selectedButton = Response.CANCEL;

        /**
         * Create a new Dialog-instance.
         * 
         * @param title
         *            The title for the headerbar.
         * @param owner
         *            The owner to position the Dialog retative to. Can be null.
         * @param message
         *            The content for the right side of the dialog.
         * @param buttons
         *            A container containing the Buttons to show.
         * @param iconFile
         *            The path to the icon-file to show. Loaded by {@link Class#getResourceAsStream(String)}.
         */
        public Dialog( final Stage owner, final String title, final Node message, final Parent buttons, final String iconFile ) {
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
                throw new MDRuntimeException( ex );
            }
            topContent.getChildren().add( message );
            final VBox vbox = new VBox( 10 );
            vbox.setPadding( new Insets( 10 ) );
            vbox.getChildren().addAll( topContent, buttons );
            Scene paramScene = new Scene( vbox );
            paramScene.getStylesheets().addAll( "/JMetroLightTheme.css", "/de/herrlock/javafx/jmetro_light_overrides.css" );
            setScene( paramScene );
        }

        /**
         * Makes the dialog visible.
         */
        public void showDialog() {
            super.sizeToScene();
            super.centerOnScreen();
            super.showAndWait();
        }

        /**
         * getter for the currently selected button
         * 
         * @return the selected button
         */
        public Response getSelectedButton() {
            return this.selectedButton;
        }

        /**
         * setter for the selected button
         * 
         * @param selectedButton
         *            the Response indicating the selected button
         */
        public void setSelectedButton( final Response selectedButton ) {
            this.selectedButton = selectedButton;
        }
    }

    /**
     * A simple {@link Text} with a preset {@link Text#getWrappingWidth() wrappingWidth} of 500
     */
    static final class Message extends Text {
        /**
         * Creates a new Message with the given String as content
         * 
         * @param msg
         *            The String to show in this Text
         */
        public Message( final String msg ) {
            super( msg );
            setWrappingWidth( 500 );
        }
    }

    /**
     * A runnable that can close a Dialog on the JavaFX-Application-Thread with its method {@link DialogHider#execute()}
     */
    public static class DialogHider implements Runnable {
        private final Dialog dialog;

        /**
         * Creates a new DialogHider with the given Dialog
         * 
         * @param dialog
         *            the Dialog to close
         */
        public DialogHider( final Dialog dialog ) {
            this.dialog = dialog;
        }

        /**
         * Calls the close-method from a Dialog
         */
        @Override
        public void run() {
            this.dialog.close();
        }

        /**
         * Runs this Runnable on the JavaFX-Application-Thread
         */
        public void execute() {
            Platform.runLater( this );
        }
    }

    private static Dialog _createDialog( final String title, final Stage owner, final Node parent, final Parent buttons,
        final String iconFile ) {
        final Task<Dialog> task = new Task<Dialog>() {
            @Override
            protected Dialog call() {
                return new Dialog( owner, title, parent, buttons, iconFile );
            }
        };
        Platform.runLater( task );
        try {
            return task.get();
        } catch ( final InterruptedException | ExecutionException ex ) {
            throw new MDRuntimeException( ex );
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
            throw new MDRuntimeException( ex );
        }
    }

    /**
     * Show a Dialog with the given parameters.
     * 
     * @param owner
     *            The parent-element for the Dialog. Can be {@code null}.
     * @param message
     *            The Node to show as message.
     * @param title
     *            The title for the dialog.
     * @param iconFile
     *            The path to the icon to show.
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

    /**
     * Show a Dialog that indicates a loading-process.
     * 
     * @param owner
     *            The parent-element for the Dialog. Can be {@code null}.
     * @param message
     *            The Node to show as message.
     * @param title
     *            The title for the dialog.
     * @param iconFile
     *            The path to the icon to show.
     * @return A DialogHider to close the Dialog on the JavaFX-Application-Thread.
     */
    public static DialogHider showLoadingDialog( final Stage owner, final Node message, final String title,
        final String iconFile ) {
        final Dialog dialog = _createDialog( title, owner, message, new VBox(), iconFile );
        dialog.initModality( Modality.NONE );
        _showDialog( dialog );
        return new DialogHider( dialog );
    }

    /**
     * Show a Dialog that indicates a loading-process.
     * 
     * @param owner
     *            The parent-element for the Dialog. Can be {@code null}.
     * @param message
     *            The String to show as message.
     * @param title
     *            The title for the dialog.
     * @param iconFile
     *            The path to the icon to show.
     * @return A DialogHider to close the Dialog on the JavaFX-Application-Thread.
     */
    public static DialogHider showLoadingDialog( final Stage owner, final String message, final String title,
        final String iconFile ) {
        return showLoadingDialog( owner, new Message( message ), title, iconFile );
    }

    /**
     * Show a Dialog that indicates a loading-process.
     * 
     * @param owner
     *            The parent-element for the Dialog. Can be {@code null}.
     * @param message
     *            The Node to show as message.
     * @param title
     *            The title for the dialog.
     * @return A DialogHider to close the Dialog on the JavaFX-Application-Thread.
     */
    public static DialogHider showLoadingDialog( final Stage owner, final Node message, final String title ) {
        return showLoadingDialog( owner, message, title, "glumanda.gif" );
    }

    /**
     * Show a Dialog that indicates a loading-process.
     * 
     * @param owner
     *            The parent-element for the Dialog. Can be {@code null}.
     * @param message
     *            The String to show as message.
     * @param title
     *            The title for the dialog.
     * @return A DialogHider to close the Dialog on the JavaFX-Application-Thread.
     */
    public static DialogHider showLoadingDialog( final Stage owner, final String message, final String title ) {
        return showLoadingDialog( owner, new Message( message ), title );
    }

    /**
     * Show a Dialog to request the user to click a button.
     * 
     * @param owner
     *            The parent-element for the Dialog. Can be {@code null}.
     * @param message
     *            The Node to show as message.
     * @param title
     *            The title for the dialog.
     * @return The {@link Response} of the confirm-dialog.
     */
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

    /**
     * Show a Dialog to request the user to click a button.
     * 
     * @param owner
     *            The parent-element for the Dialog. Can be {@code null}.
     * @param message
     *            The String to show as message.
     * @param title
     *            The title for the dialog.
     * @return The {@link Response} of the confirm-dialog.
     */
    public static Response showConfirmDialog( final Stage owner, final String message, final String title ) {
        return showConfirmDialog( owner, new Message( message ), title );
    }

    /**
     * Show a Dialog with a Message and an information-icon.
     * 
     * @param owner
     *            The parent-element for the Dialog. Can be {@code null}.
     * @param message
     *            The String to show as message.
     * @param title
     *            The title for the dialog.
     */
    public static void showMessageDialog( final Stage owner, final String message, final String title ) {
        showMessageDialog( owner, new Message( message ), title );
    }

    /**
     * Show a Dialog with a Message and an information-icon.
     * 
     * @param owner
     *            The parent-element for the Dialog. Can be {@code null}.
     * @param message
     *            The Node to show as message.
     * @param title
     *            The title for the dialog.
     */
    public static void showMessageDialog( final Stage owner, final Node message, final String title ) {
        showDialog( owner, message, title, "dialog-information.png" );
    }

    /**
     * Show a Dialog with a Message and a warning-icon.
     * 
     * @param owner
     *            The parent-element for the Dialog. Can be {@code null}.
     * @param message
     *            The String to show as message.
     * @param title
     *            The title for the dialog.
     */
    public static void showWarningDialog( final Stage owner, final String message, final String title ) {
        showWarningDialog( owner, new Message( message ), title );
    }

    /**
     * Show a Dialog with a Message and a warning-icon.
     * 
     * @param owner
     *            The parent-element for the Dialog. Can be {@code null}.
     * @param message
     *            The Node to show as message.
     * @param title
     *            The title for the dialog.
     */
    public static void showWarningDialog( final Stage owner, final Node message, final String title ) {
        showDialog( owner, message, title, "dialog-warning.png" );
    }

    /**
     * Show a Dialog with a Message and an error-icon.
     * 
     * @param owner
     *            The parent-element for the Dialog. Can be {@code null}.
     * @param message
     *            The String to show as message.
     * @param title
     *            The title for the dialog.
     */
    public static void showErrorDialog( final Stage owner, final String message, final String title ) {
        showErrorDialog( owner, new Message( message ), title );
    }

    /**
     * Show a Dialog with a Message and an error-icon.
     * 
     * @param owner
     *            The parent-element for the Dialog. Can be {@code null}.
     * @param message
     *            The Node to show as message.
     * @param title
     *            The title for the dialog.
     */
    public static void showErrorDialog( final Stage owner, final Node message, final String title ) {
        showDialog( owner, message, title, "dialog-error.png" );
    }

    private Dialogs() {
        // not used
    }

}
