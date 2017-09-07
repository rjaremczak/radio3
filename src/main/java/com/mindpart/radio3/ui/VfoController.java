package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.Radio3;
import com.mindpart.types.Frequency;
import javafx.event.ActionEvent;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class VfoController extends ComponentController {
    private final Radio3 radio3;
    private final BundleData bundle;

    public VfoController(Radio3 radio3, BundleData bundle) {
        this.radio3 = radio3;
        this.bundle = bundle;
    }

    @Override
    public void initialize() {
        setUp(bundle.resolve("label.vfo"), bundle.resolve("label.frequency"), true, bundle.buttonSet);
    }


    @Override
    public void onMainButton(ActionEvent actionEvent) {
        Frequency frequency = Frequency.parse(valueField.getText());
        if(frequency != null) {
            radio3.writeVfoFrequency((int) frequency.toHz());
            update(frequency);
        }
    }

    public void update(Frequency frequency) {
        setValue(frequency.format());
    }
}
