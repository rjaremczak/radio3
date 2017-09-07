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
    private final Radio3 radio3;
    private final BundleData bundle;

    public FreqMeterController(Radio3 radio3, BundleData bundle) {
        this.radio3 = radio3;
        this.bundle = bundle;
    }

    @Override
    public void initialize() {
        setUp(bundle.resolve("label.freqMeter"), bundle.resolve("label.frequency"), false, bundle.buttonGet);
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
