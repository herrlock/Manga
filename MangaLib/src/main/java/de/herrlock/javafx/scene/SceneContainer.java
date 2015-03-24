package de.herrlock.javafx.scene;

import java.util.Arrays;
import java.util.Collection;

import javafx.scene.Scene;
import javafx.scene.image.Image;

public abstract class SceneContainer {
    private Scene scene;

    public Scene getScene() {
        return this.scene;
    }

    protected void setScene( Scene scene ) {
        this.scene = scene;
    }

    public Collection<Image> getIcons() {
        return Arrays.asList();
    }

    public Collection<String> getStylesheets() {
        return Arrays.asList();
    }

    public abstract String getTitle();
}
