package com.mindpart.javafx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.shape.Line;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.18
 */
public class ChartRuler<T> {
    private final ObjectProperty<T> valueProperty;
    private final Line line;

    public ChartRuler(T value, Line line) {
        this.valueProperty = new SimpleObjectProperty<>(value);
        this.line = line;
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

    public Line getNode() {
        return line;
    }

    public void update(double startX, double startY, double endX, double endY) {
        line.setStartX(startX);
        line.setStartY(startY);
        line.setEndX(endX);
        line.setEndY(endY);
        line.toFront();
    }
}
