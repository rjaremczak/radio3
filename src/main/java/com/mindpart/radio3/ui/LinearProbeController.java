package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.Radio3;
import com.mindpart.radio3.device.Response;
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
        Response<Double> response = radio3.readLinProbe();
        if(response.isOK()) update(response.getData());
    }

    @Override
    public void initialize() {
        setUpAsProbe("Linear Probe", "Vrms");
    }

    public void update(double power) {
        setValue(fmtPower.format(power * 1000)+" mV");
    }
}
