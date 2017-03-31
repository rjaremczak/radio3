package com.mindpart.radio3.ui;

import com.mindpart.types.Frequency;
import javafx.event.ActionEvent;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class VfoController extends ComponentController {
    private Radio3 radio3;

    public VfoController(Radio3 radio3) {
        this.radio3 = radio3;
    }

    @Override
    public void initialize() {
        setUp("VFO", "Frequency", true, "Set");
    }


    @Override
    public void onMainButton(ActionEvent actionEvent) {
        Frequency frequency = Frequency.parse(valueField.getText());
        if(frequency != null) {
            radio3.getDeviceService().writeVfoFrequency((int) frequency.toHz());
            update(frequency);
        }
    }

    public void update(Frequency frequency) {
        setValue(frequency.format());
    }
}
