package com.mindpart.ui;

import javafx.beans.property.ObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.util.function.UnaryOperator;

/**
 * Created by Robert Jaremczak
 * Date: 2017.10.28
 */
public class ValueField<V> {
    private final TextField textField;
    private final TextFormatter<V> textFormatter;

    public ValueField(StringConverter<V> valueConverter, V defaultValue, UnaryOperator<TextFormatter.Change> filter) {
        textField = new TextField();
        textFormatter = new TextFormatter<>(valueConverter, defaultValue, filter);
        textField.setTextFormatter(textFormatter);
    }

    public Node getNode() {
        return textField;
    }

    public ObjectProperty<V> valueProperty() {
        return textFormatter.valueProperty();
    }
}
