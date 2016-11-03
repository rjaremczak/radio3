package com.mindpart.ui;

import com.mindpart.types.Frequency;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.control.TextField;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.03
 */
public class FrequencyField extends TextField {
    private ObjectProperty<Frequency> frequencyProperty = new SimpleObjectProperty<>();
    private Runnable onChangeHandler = null;

    public FrequencyField() {
        setFrequency(Frequency.fromHz(0));
        initHandlers();
    }

    private void initHandlers() {
        setOnAction(event -> reformat());

        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if(!newValue.booleanValue()) {
                reformat();
            }
        });

        frequencyProperty.addListener((observable, oldValue, newValue) -> {
            setText(newValue.format());
            if(onChangeHandler!=null && !oldValue.equals(newValue)) {
                onChangeHandler.run();
            }
        });
    }

    public void setOnChangeHandler(Runnable onChangeHandler) {
        this.onChangeHandler = onChangeHandler;
    }

    private void reformat() {
        setFrequency(Frequency.parse(getText()));
        selectAll();
    }

    public Frequency getFrequency() {
        return frequencyProperty.get();
    }

    public void setFrequency(Frequency frequency) {
        this.frequencyProperty.set(frequency);
    }

    public void clearOnChangeHandler() {
        this.onChangeHandler = null;
    }
}
