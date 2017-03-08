package com.mindpart.ui;

import com.mindpart.types.Frequency;
import com.mindpart.utils.FxUtils;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.function.Supplier;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.03
 */
public class FrequencyField extends TextField {
    private static final NumberFormat IN_FORMAT = new DecimalFormat("0.###");
    private static final NumberFormat OUT_FORMAT = new DecimalFormat("0.000");

    private Runnable onChangeHandler = null;
    private Supplier<Frequency> minSupplier = () -> Frequency.ZERO;
    private Supplier<Frequency> maxSupplier = () -> Frequency.ofHz(Long.MAX_VALUE);
    private Frequency frequency;
    private String lastValue;
    private String label;

    private volatile boolean inActionEvent = false;

    public FrequencyField() {
        setFrequency(Frequency.ZERO);
        initHandlers();
    }

    private void initHandlers() {
        setOnAction(event -> {
            inActionEvent = true;
            try {
                if(parse(getText())) {
                    format();
                    callOnChangeHandler();
                } else {
                    event.consume();
                    requestFocus();
                }
            } finally {
                inActionEvent = false;
            }
        });

        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!inActionEvent && !newValue.booleanValue()) {
                if(parse(getText())) {
                    format();
                    callOnChangeHandler();
                } else {
                    requestFocus();
                }
            }
        });
    }

    public void setOnChangeHandler(Runnable onChangeHandler) {
        this.onChangeHandler = onChangeHandler;
    }

    public void clearOnChangeHandler() {
        this.onChangeHandler = null;
    }

    private void callOnChangeHandler() {
        if(onChangeHandler!=null) {
            onChangeHandler.run();
        }
    }

    private boolean parse(String str) {
        Frequency parsed;

        try {
            parsed = Frequency.ofMHz(IN_FORMAT.parse(str).doubleValue());
        } catch (NumberFormatException|ParseException e) {
            FxUtils.alertInputError(getLabel(), "malformed value", "must be a valid frequency");
            setText(lastValue);
            return false;
        }

        Frequency min = minSupplier.get();
        Frequency max = maxSupplier.get();
        if(max.compareTo(min) <= 0) {
            new IllegalArgumentException("maximum must be greater than minimum");
        }

        if(parsed.inRange(min, max)) {
            this.frequency = parsed;
            return true;
        } else {
            try {
                FxUtils.alertInputError(getLabel(), "value out of range", "must be between "+min+" and "+max);
                setText(lastValue);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return false;
    }

    public Frequency getFrequency() {
        return frequency;
    }

    public void setFrequency(Frequency frequency) {
        lastValue = getText();
        this.frequency = frequency;
        format();
    }

    private void format() {
        setText(OUT_FORMAT.format(frequency.toMHz()));
    }

    public void setMinSupplier(Supplier<Frequency> minSupplier) {
        this.minSupplier = minSupplier;
    }

    public void setMaxSupplier(Supplier<Frequency>  maxSupplier) {
        this.maxSupplier = maxSupplier;
    }

    public String getLabel() {
        return StringUtils.isEmpty(label) ? "input field" : label;
    }

    public void setLabel(String label) {
        this.label = label;
    }
}
