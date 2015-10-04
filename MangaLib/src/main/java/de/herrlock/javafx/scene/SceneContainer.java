package de.herrlock.javafx.scene;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javafx.scene.Scene;
import javafx.scene.image.Image;

/**
 * @author HerrLock
 */
public abstract class SceneContainer {
    private Scene scene;

    /**
     * Getter for this Container's {@link Scene}
     * 
     * @return a scene
     */
    public Scene getScene() {
        return this.scene;
    }

    /**
     * Setter for this container's {@link Scene}
     * 
     * @param scene
     *            the Scene to set
     */
    protected void setScene( Scene scene ) {
        this.scene = scene;
    }

    /**
     * A {@link List} of {@link Image}s to show. Can be Overridden to define own Icons; defaults to an empty List
     * 
     * @return the icons to use
     */
    public Collection<Image> getIcons() {
        return Arrays.asList();
    }

    /**
     * A {@link List} of URLs in String-format to load CSS-stylesheets from. Can be Overridden to load own Stylesheets; defaults
     * to an empty List
     * 
     * @return the stylesheets to include
     */
    public Collection<String> getStylesheets() {
        return Arrays.asList();
    }

    /**
     * Defines the title for the contained Scene
     * 
     * @return the title fo rthe Scene
     */
    public abstract String getTitle();
}
