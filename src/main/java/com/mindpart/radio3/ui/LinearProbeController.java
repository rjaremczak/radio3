package com.mindpart.radio3.ui;

import com.mindpart.radio3.LinearProbe;
import javafx.event.ActionEvent;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class LinearProbeController extends FeatureController {
    private static final NumberFormat fmtPower = new DecimalFormat("0.000");
    private LinearProbe linearProbe;

    public LinearProbeController(LinearProbe linearProbe) {
        this.linearProbe = linearProbe;
    }

    @Override
    public void onMainButton(ActionEvent actionEvent) {
        linearProbe.requestData();
    }

    @Override
    public void initialize() {
        setUpAsProbe("Linear Probe", "Vrms");
    }

    public void update(double power) {
        setValue(fmtPower.format(power * 1000)+" mV");
    }
}
