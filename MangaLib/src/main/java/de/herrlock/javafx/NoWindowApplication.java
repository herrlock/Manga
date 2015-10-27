package de.herrlock.javafx;

import javafx.stage.Stage;

public abstract class NoWindowApplication extends AbstractApplication {

    @Override
    public abstract void start( final Stage stage );
}
