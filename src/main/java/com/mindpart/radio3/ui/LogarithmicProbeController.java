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
public class LogarithmicProbeController extends ComponentController {
    private static final NumberFormat fmtPower = new DecimalFormat("0.000");

    private final Radio3 radio3;
    private final UserInterface ui;

    public LogarithmicProbeController(Radio3 radio3, UserInterface ui) {
        this.radio3 = radio3;
        this.ui = ui;
    }

    @Override
    public void onMainButton(ActionEvent actionEvent) {
        Response<Double> response = radio3.readLogProbe();
        if(response.isOK()) update(response.getData());
    }

    @Override
    public void initialize() {
        setUp(ui.text("label.logProbe"), ui.text("label.power"), false, ui.text("button.get"));
    }

    public void update(double power) {
        setValue(fmtPower.format(power)+" dBm");
    }
}
