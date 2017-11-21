package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.Radio3;
import com.mindpart.radio3.device.Response;
import javafx.event.ActionEvent;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class FreqMeterController extends ComponentController {
    private final Radio3 radio3;
    private final UserInterface ui;

    public FreqMeterController(Radio3 radio3, UserInterface ui) {
        this.radio3 = radio3;
        this.ui = ui;
    }

    @Override
    public void initialize() {
        setUp(ui.text("label.freqMeter"), ui.text("label.frequency"), false, ui.text("button.get"));
    }

    @Override
    public void onMainButton(ActionEvent actionEvent) {
        Response<Integer> response = radio3.readFMeter();
        if(response.isOK()) update(response.getData());
    }

    public void update(int frequency) {
        setValue(ui.frequency.format(frequency));
    }
}
