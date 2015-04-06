package de.herrlock.manga.util;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

public class ExecHandlerTask extends Task<Void> {
    private final Exec exec;

    public ExecHandlerTask( Exec exec ) {
        this.exec = exec;
        setOnFailed( new ExceptionHandler() );
    }

    @Override
    protected Void call() {
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
