package com.mindpart.radio3.ui;

import javafx.event.ActionEvent;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class LinearProbeController extends ComponentController {
    private static final NumberFormat fmtPower = new DecimalFormat("0.000");

    private Radio3 radio3;

    public LinearProbeController(Radio3 radio3) {
        this.radio3 = radio3;
    }

    @Override
    public void onMainButton(ActionEvent actionEvent) {
        radio3.requestLinearProbeSample();
    }

    @Override
    public void initialize() {
        setUpAsProbe("Linear Probe", "Vrms");
    }

    public void update(double power) {
        setValue(fmtPower.format(power * 1000)+" mV");
    }
}
