package com.mindpart.radio3.ui;

import com.mindpart.radio3.FMeterUnit;
import javafx.event.ActionEvent;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class FMeterController extends FeatureController {
    private FMeterUnit fMeterUnit;

    public FMeterController(FMeterUnit fMeterUnit) {
        this.fMeterUnit = fMeterUnit;
    }

    @Override
    public void initialize() {
        setUpAsProbe("F-Meter", "Frequency", "Hz");
    }

    @Override
    public void onMainButton(ActionEvent actionEvent) {
        fMeterUnit.requestData();
    }

    public void setFrequency(Long frequency) {
        setValue(frequency.toString());
    }
}
