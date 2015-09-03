package de.herrlock.javafx.scene;

import javafx.scene.Node;

/**
 * @author HerrLock
 */
public abstract class NodeContainer {
    private final Node node = createNode();

    /**
     * creates a Node that can be accessed with {@link #getNode()}
     * 
     * @return a Node
     */
    protected abstract Node createNode();

    /**
     * Getter for this container's {@link Node}
     * 
     * @return the node stored
     */
    public Node getNode() {
        return this.node;
    }
}
