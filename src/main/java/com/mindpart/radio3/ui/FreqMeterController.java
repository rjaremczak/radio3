package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.Radio3;
import com.mindpart.radio3.device.Response;
import com.mindpart.types.Frequency;
import javafx.event.ActionEvent;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class FreqMeterController extends ComponentController {
    private Radio3 radio3;

    public FreqMeterController(Radio3 radio3) {
        this.radio3 = radio3;
    }

    @Override
    public void initialize() {
        setUpAsProbe("F-Meter", "Frequency");
    }

    @Override
    public void onMainButton(ActionEvent actionEvent) {
        Response<Frequency> response = radio3.readFMeter();
        if(response.isOK()) update(response.getData());
    }

    public void update(Frequency frequency) {
        setValue(frequency.format());
    }
}
