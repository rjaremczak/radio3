package com.mindpart.radio3.ui;

import com.mindpart.radio3.LogarithmicProbe;
import com.mindpart.radio3.device.DeviceService;
import javafx.event.ActionEvent;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class LogarithmicProbeController extends FeatureController {
    private static final NumberFormat fmtPower = new DecimalFormat("0.000");

    private LogarithmicProbe logarithmicProbe;

    public LogarithmicProbeController(LogarithmicProbe logarithmicProbe) {
        this.logarithmicProbe = logarithmicProbe;
    }

    @Override
    public void onMainButton(ActionEvent actionEvent) {
        logarithmicProbe.requestData();
    }

    @Override
    public void initialize() {
        setUpAsProbe("Logarithmic Probe", "Power", "dBm");
    }

    public void update(double power) {
        setValue(fmtPower.format(power));
    }
}
