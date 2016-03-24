package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.DeviceService;
import javafx.event.ActionEvent;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class FMeterController extends ProbeController {
    private DeviceService deviceService;

    public FMeterController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public void initialize() {
        setUp("F-Meter", "Frequency", "Hz");
    }

    @Override
    public void onMainButton(ActionEvent actionEvent) {
        deviceService.readFrequency();
    }

    public void setFrequency(Long frequency) {
        setValue(frequency.toString());
    }
}
