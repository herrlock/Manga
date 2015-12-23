package de.herrlock.javafx;

import javafx.stage.Stage;

/**
 * An {@link javafx.application.Application Application} that does not provide a window but can start the
 * JavaFX-Application-Thread
 * 
 * @author HerrLock
 */
public abstract class NoWindowApplication extends AbstractApplication {

    /**
     * shorthand for {@linkplain #start(Stage)} called with {@code null}
     */
    public void start() {
        this.start( null );
    }

    @Override
    public abstract void start( final Stage stage );
}
