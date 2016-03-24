package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.DeviceService;
import javafx.event.ActionEvent;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class LogarithmicProbeController extends ProbeController {
    private DeviceService deviceService;

    public LogarithmicProbeController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public void onMainButton(ActionEvent actionEvent) {
        deviceService.readLogarithmicProbe();
    }

    @Override
    public void initialize() {
        setUp("Logarithmic Probe", "Gain", "dB");
    }

    public void setGain(double gain) {
        setValue(Double.toString(gain));
    }
}
