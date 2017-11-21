package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.Radio3;
import com.mindpart.type.Frequency;
import javafx.event.ActionEvent;
import org.apache.commons.lang3.math.NumberUtils;

import static com.mindpart.type.UnitPrefix.MEGA;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class VfoController extends ComponentController {
    private final Radio3 radio3;
    private final UserInterface ui;

    public VfoController(Radio3 radio3, UserInterface ui) {
        this.radio3 = radio3;
        this.ui = ui;
    }

    @Override
    public void initialize() {
        setUp(ui.text("label.vfo"), ui.text("label.frequency"), true, ui.text("button.set"));
    }


    @Override
    public void onMainButton(ActionEvent actionEvent) {
        double freqMHz = NumberUtils.toDouble(valueField.getText());
        if(freqMHz>0) {
            int freqHz = (int) MEGA.toBase(freqMHz);
            radio3.writeVfoFrequency(freqHz);
            update(freqHz);
        }
    }

    public void update(Integer frequency) {
        setValue(ui.frequency.format(MEGA.fromBase(frequency)));
    }
}
