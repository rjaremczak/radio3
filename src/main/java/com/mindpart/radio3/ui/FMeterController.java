package com.mindpart.radio3.ui;

import com.mindpart.types.Frequency;
import javafx.event.ActionEvent;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class FMeterController extends ComponentController {
    private Radio3 radio3;

    public FMeterController(Radio3 radio3) {
        this.radio3 = radio3;
    }

    @Override
    public void initialize() {
        setUpAsProbe("F-Meter", "Frequency");
    }

    @Override
    public void onMainButton(ActionEvent actionEvent) {
        radio3.requestFMeterSample();
    }

    public void setFrequency(Frequency frequency) {
        setValue(frequency.format());
    }
}
