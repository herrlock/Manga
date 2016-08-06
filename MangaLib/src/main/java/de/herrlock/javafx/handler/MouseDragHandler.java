package de.herrlock.javafx.handler;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javafx.event.EventHandler;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;

/**
 * @author HerrLock
 */
public final class MouseDragHandler implements EventHandler<MouseEvent> {
    private static final Logger logger = LogManager.getLogger();
    private final Stage stage;
    private double offsetX, offsetY;

    public MouseDragHandler( final Stage stage ) {
        this.stage = stage;
    }

    @Override
    public void handle( final MouseEvent event ) {
        logger.traceEntry( "event: {}", event );
        double screenX = event.getScreenX();
        double screenY = event.getScreenY();
        if ( event.getEventType().getName().equals( "MOUSE_PRESSED" ) ) {
            this.offsetX = this.stage.getX() - screenX;
            this.offsetY = this.stage.getY() - screenY;
        } else {
            this.stage.setX( screenX + this.offsetX );
            this.stage.setY( screenY + this.offsetY );
        }
    }

}
