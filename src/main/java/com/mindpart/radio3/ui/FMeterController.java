package com.mindpart.radio3.ui;

import com.mindpart.radio3.FMeterProbe;
import javafx.event.ActionEvent;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class FMeterController extends FeatureController {
    private FMeterProbe fMeterProbe;

    public FMeterController(FMeterProbe fMeterProbe) {
        this.fMeterProbe = fMeterProbe;
    }

    @Override
    public void initialize() {
        setUpAsProbe("F-Meter", "Frequency", "MHz");
    }

    @Override
    public void onMainButton(ActionEvent actionEvent) {
        fMeterProbe.requestData();
    }

    public void setFrequency(Double frequency) {
        setValue(frequency.toString());
    }
}
