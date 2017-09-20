package com.mindpart.javafx;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.20
 */
public class ChartSpanMarker<T> {
    private final ObjectProperty<T> minValueProperty;
    private final ObjectProperty<T> maxValueProperty;
    private final Rectangle rectangle;

    public ChartSpanMarker(T min, T max, Color fill) {
        minValueProperty = new SimpleObjectProperty<>(min);
        maxValueProperty = new SimpleObjectProperty<>(max);
        rectangle = new Rectangle();
        rectangle.setFill(fill);
    }

    public void setSpan(T min, T max) {
        minValueProperty.setValue(min);
        maxValueProperty.setValue(max);
    }

    public T getMinValue() {
        return minValueProperty.getValue();
    }

    public T getMaxValue() {
        return maxValueProperty.getValue();
    }

    public ObjectProperty<T> minValueProperty() {
        return minValueProperty;
    }

    public ObjectProperty<T> maxValueProperty() {
        return maxValueProperty;
    }

    public void update(double x0, double y0, double x1, double y1) {
        rectangle.setX(x0);
        rectangle.setY(y0);
        rectangle.setWidth(x1 - x0);
        rectangle.setHeight(y1 - y0);
        rectangle.toFront();
    }

    public Node getNode() {
        return rectangle;
    }
}
