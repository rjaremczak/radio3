package com.mindpart.javafx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.18
 */
public class ChartRuler<T> {
    private final ObjectProperty<T> valueProperty;
    private Node node;

    public ChartRuler(T value, Node node) {
        this.valueProperty = new SimpleObjectProperty<>(value);
        this.node = node;
    }

    public void setValue(T value) {
        valueProperty.setValue(value);
    }

    public T getValue() {
        return valueProperty.getValue();
    }

    public ObjectProperty<T> valueProperty() {
        return valueProperty;
    }

    public Node getNode() {
        return node;
    }

    public void setNode(Node node) {
        this.node = node;
    }
}
