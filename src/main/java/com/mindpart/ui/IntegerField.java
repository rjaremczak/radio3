package com.mindpart.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.IntegerStringConverter;

import java.util.regex.Pattern;

/**
 * Created by Robert Jaremczak
 * Date: 2017.10.28
 */
public class IntegerField extends Spinner<Integer> {
    private static final Pattern NON_NEGATIVE = Pattern.compile("[0-9]*");
    private final TextFormatter<Integer> textFormatter;

    public IntegerField() {
        super(1, Integer.MAX_VALUE, 10, 1);
        textFormatter = new TextFormatter<>(
                new IntegerStringConverter(),
                getValue(),
                change -> NON_NEGATIVE.matcher(change.getControlNewText()).matches() ? change : null
        );
        getEditor().setTextFormatter(textFormatter);
        getEditor().setAlignment(Pos.BASELINE_RIGHT);
        setEditable(true);
    }
}
