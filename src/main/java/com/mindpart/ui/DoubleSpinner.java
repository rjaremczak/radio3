package com.mindpart.ui;

import javafx.geometry.Pos;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;
import org.apache.log4j.Logger;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.regex.Pattern;

/**
 * Created by Robert Jaremczak
 * Date: 2017.10.28
 */
public class DoubleSpinner extends Spinner<Double> {
    private static final Logger logger = Logger.getLogger(DoubleSpinner.class);
    private static final Pattern NON_NEGATIVE = Pattern.compile("\\d+(?:\\.\\d+)?");

    private final TextFormatter<Double> textFormatter;

    private NumberFormat valueFormatter = new DecimalFormat();

    public DoubleSpinner() {
        super(0, Double.MAX_VALUE, 0, 1);
        textFormatter = new TextFormatter<>(
                new StringConverter<Double>() {
                    @Override
                    public String toString(Double object) {
                        return valueFormatter.format(object);
                    }

                    @Override
                    public Double fromString(String string) {
                        try {
                            return valueFormatter.parse(string).doubleValue();
                        } catch (ParseException e) {
                            logger.error("malformed double", e);
                            return 0.0;
                        }
                    }
                },
                getValue(),
                change -> NON_NEGATIVE.matcher(change.getControlNewText()).matches() ? change : null
        );
        getEditor().setTextFormatter(textFormatter);
        getEditor().setAlignment(Pos.BASELINE_RIGHT);
        setEditable(true);
    }

    public void setDecimalFormat(String format) {
        valueFormatter = new DecimalFormat(format);
    }

    public SpinnerValueFactory.DoubleSpinnerValueFactory getDoubleValueFactory() {
        return (SpinnerValueFactory.DoubleSpinnerValueFactory) getValueFactory();
    }
}
