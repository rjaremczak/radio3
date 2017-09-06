package com.mindpart.ui;

import com.mindpart.radio3.ui.BundleData;
import com.mindpart.types.Frequency;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.TextField;
import org.apache.commons.lang3.StringUtils;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.ResourceBundle;
import java.util.function.Supplier;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.03
 */
public class FrequencyField extends TextField {
    private static final NumberFormat IN_FORMAT = new DecimalFormat("0.###");
    private static final NumberFormat OUT_FORMAT = new DecimalFormat("0.000");

    private Runnable changeListener = null;
    private Supplier<Frequency> minSupplier = () -> Frequency.ZERO;
    private Supplier<Frequency> maxSupplier = () -> Frequency.ofHz(Long.MAX_VALUE);
    private Frequency frequency;
    private String lastValue;
    private String label;

    private String textMalformed;
    private String textMalformedDetails;
    private String textOutOfRange;
    private String textOutOfRangeDetails;

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
                    runChangeListener();
                } else {
                    event.consume();
                    requestFocus();
                }
            } finally {
                inActionEvent = false;
            }
        });

        focusedProperty().addListener(this::internalChangeListener);
    }

    void internalChangeListener(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) {
        if (!inActionEvent && !newValue) {
            if(parse(getText())) {
                format();
                runChangeListener();
            } else {
                requestFocus();
            }
        }
    }

    public void setChangeListener(Runnable changeListener) {
        focusedProperty().addListener(this::internalChangeListener);
        this.changeListener = changeListener;
    }

    private void runChangeListener() {
        if(changeListener !=null) {
            changeListener.run();
        }
    }

    private boolean parse(String str) {
        Frequency parsed;

        try {
            parsed = Frequency.ofMHz(IN_FORMAT.parse(str).doubleValue());
        } catch (NumberFormatException|ParseException e) {
            FxUtils.alertInputError(getLabel(), textMalformed, textMalformedDetails);
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
                FxUtils.alertInputError(getLabel(), textOutOfRange, String.format(textOutOfRangeDetails, min, max));
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

    public void initFromBundle(BundleData bundle) {
        textMalformed = bundle.resolve("frequencyField.malformed");
        textMalformedDetails = bundle.resolve("frequencyField.malformed.details");
        textOutOfRange = bundle.resolve("frequencyField.outOfRange");
        textOutOfRangeDetails = bundle.resolve("frequencyField.outOfRange.details");
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
