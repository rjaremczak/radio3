package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.DeviceService;
import javafx.event.ActionEvent;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class LinearProbeController extends FeatureController {
    private DeviceService deviceService;

    public LinearProbeController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public void onMainButton(ActionEvent actionEvent) {
        deviceService.getLinearProbe();
    }

    @Override
    public void initialize() {
        setUpAsProbe("Linear Probe", "Gain", "x");
    }

    public void setGain(double gain) {
        setValue(Double.toString(gain));
    }
}
