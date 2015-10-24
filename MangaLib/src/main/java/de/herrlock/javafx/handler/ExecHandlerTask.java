package de.herrlock.javafx.handler;

import javax.swing.JOptionPane;

import de.herrlock.manga.exceptions.InitializeException;
import de.herrlock.manga.exceptions.MyException;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

/**
 * The bridge between an {@link Exec} and a {@link Task}. Can be used or extended
 * 
 * @author Herrlock
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

    @Override
    protected Void call() {
        boolean exit = true;
        try {
            this.exec.execute();
        } catch ( final InitializeException ex ) {
            exit = false;
            JOptionPane.showMessageDialog( null, ex.getMessage() );
        } finally {
            if ( exit ) {
                Platform.exit();
            }
        }
        return null;
    }

    final class ExceptionHandler implements EventHandler<WorkerStateEvent> {
        @Override
        public void handle( final WorkerStateEvent t ) {
            Throwable exception = ExecHandlerTask.this.getException();
            throw exception instanceof RuntimeException ? ( RuntimeException ) exception : new MyException( exception );
        }
    }
}
