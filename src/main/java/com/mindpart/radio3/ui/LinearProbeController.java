package com.mindpart.radio3.ui;

import com.mindpart.radio3.LinearProbe;
import com.mindpart.radio3.device.DeviceService;
import javafx.event.ActionEvent;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class LinearProbeController extends FeatureController {
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
        setUpAsProbe("Linear Probe", "Power", "mW");
    }

    public void setGain(double gain) {
        setValue(Double.toString(gain));
    }
}
