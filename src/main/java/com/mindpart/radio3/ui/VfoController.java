package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.DeviceService;
import com.mindpart.types.Frequency;
import javafx.event.ActionEvent;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class VfoController extends ComponentController {
    private DeviceService deviceService;

    public VfoController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public void initialize() {
        setUp("VFO", "Frequency", true, "Set");
    }


    @Override
    public void onMainButton(ActionEvent actionEvent) {
        Frequency frequency = Frequency.parse(valueField.getText());
        deviceService.setVfoFrequency((int)frequency.toHz());
        setFrequency(frequency);
    }

    public void setFrequency(Frequency frequency) {
        setValue(frequency.format());
    }
}
