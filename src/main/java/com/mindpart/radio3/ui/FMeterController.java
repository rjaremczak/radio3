package com.mindpart.radio3.ui;

import com.mindpart.radio3.FMeterProbe;
import com.mindpart.types.Frequency;
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
        setUpAsProbe("F-Meter", "Frequency");
    }

    @Override
    public void onMainButton(ActionEvent actionEvent) {
        fMeterProbe.requestData();
    }

    public void setFrequency(Frequency frequency) {
        setValue(frequency.format());
    }
}
