package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.DeviceService;
import javafx.event.ActionEvent;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class VfoController extends FeatureController {
    private DeviceService deviceService;

    public VfoController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public void initialize() {
        setUp("VFO", "Frequency", true, "Hz", "Set");
    }


    @Override
    public void onMainButton(ActionEvent actionEvent) {
        int frequency = Integer.parseInt(valueField.getText());
        deviceService.setVfoFrequency(frequency);
    }

    public void setFrequency(Long frequency) {
        setValue(frequency.toString());
    }
}
