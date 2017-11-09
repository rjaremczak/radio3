package com.mindpart.ui;

import javafx.scene.control.Spinner;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.util.function.UnaryOperator;

/**
 * Created by Robert Jaremczak
 * Date: 2017.10.28
 */
public class ValueField<V> extends Spinner<V> {
    private final TextFormatter<V> textFormatter;

    public ValueField(StringConverter<V> valueConverter, V defaultValue, UnaryOperator<TextFormatter.Change> filter) {
        super();
        textFormatter = new TextFormatter<>(valueConverter, defaultValue, filter);
        getEditor().setTextFormatter(textFormatter);
    }
}
