package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.DeviceService;
import javafx.event.ActionEvent;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class FMeterController extends FeatureController {
    private DeviceService deviceService;

    public FMeterController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public void initialize() {
        setUpAsProbe("F-Meter", "Frequency", "Hz");
    }

    @Override
    public void onMainButton(ActionEvent actionEvent) {
        deviceService.getFMeter();
    }

    public void setFrequency(Long frequency) {
        setValue(frequency.toString());
    }
}
