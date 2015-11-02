package de.herrlock.javafx;

import javafx.stage.Stage;

public abstract class NoWindowApplication extends AbstractApplication {

    public void start() {
        this.start( null );
    }

    @Override
    public abstract void start( final Stage stage );
}
