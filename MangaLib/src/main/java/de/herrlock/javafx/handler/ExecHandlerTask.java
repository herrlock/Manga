package de.herrlock.javafx.handler;

import de.herrlock.javafx.Dialogs;
import de.herrlock.manga.exceptions.InitializeException;
import de.herrlock.manga.exceptions.MDRuntimeException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

/**
 * The bridge between an {@link Exec} and a {@link Task}. Can be used or extended.
 * 
 * @author HerrLock
 */
public class ExecHandlerTask extends Task<Void> {
    private final Exec exec;

    /**
     * A new Task that will execute the given Exec
     * 
     * @param exec
     *            the {@link Exec} to execute
     */
    public ExecHandlerTask( final Exec exec ) {
        this.exec = exec;
        setOnFailed( new ExceptionHandler() );
    }

    /**
     * Calls {@link Exec#execute()}. Calls {@link Platform#exit()}, when the Exec ran without Exception.
     * 
     * @return Always {@code null}.
     */
    @Override
    protected Void call() {
        boolean exit = true;
        try {
            this.exec.execute();
        } catch ( final InitializeException ex ) {
            exit = false;
            Dialogs.showMessageDialog( null, ex.getMessage(), ex.getMessage() );
        } finally {
            if ( exit ) {
                Platform.exit();
            }
        }
        return null;
    }

    /**
     * An ExceptionHandler that throws rethrows a {@link RuntimeException} and wraps other Exceptions in a
     * {@link MDRuntimeException}
     * 
     * @author HerrLock
     */
    final class ExceptionHandler implements EventHandler<WorkerStateEvent> {
        @Override
        public void handle( final WorkerStateEvent t ) {
            Throwable exception = ExecHandlerTask.this.getException();
            throw exception instanceof RuntimeException ? ( RuntimeException ) exception : new MDRuntimeException( exception );
        }
    }
}
