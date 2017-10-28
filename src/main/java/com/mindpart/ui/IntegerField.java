package com.mindpart.ui;

import javafx.util.converter.IntegerStringConverter;

import java.util.regex.Pattern;

/**
 * Created by Robert Jaremczak
 * Date: 2017.10.28
 */
public class IntegerField extends ValueField<Integer> {
    private static final Pattern NON_NEGATIVE = Pattern.compile("[0-9]*");

    public IntegerField() {
        super(new IntegerStringConverter(), null, change -> NON_NEGATIVE.matcher(change.getControlNewText()).matches() ? change : null);
    }
}
