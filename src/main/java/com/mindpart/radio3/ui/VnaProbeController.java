package com.mindpart.radio3.ui;

import com.mindpart.radio3.VnaResult;
import javafx.event.ActionEvent;
import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class VnaProbeController extends ComponentController {
    private static final NumberFormat fmtSwr = new DecimalFormat("0.0 Ω");
    private static final NumberFormat fmtRX = new DecimalFormat("0.0 Ω");

    private Radio3 radio3;

    public VnaProbeController(Radio3 radio3) {
        this.radio3 = radio3;
    }

    @Override
    public void onMainButton(ActionEvent actionEvent) {
        radio3.requestVnaProbeSample();
    }

    @Override
    public void initialize() {
        setUpAsProbe("VNA Probe", "SWR / R / X");
    }

    public void update(VnaResult vnaResult) {
        valueField.setText(fmtSwr.format(vnaResult.getSwr()) + " / "+fmtRX.format(vnaResult.getR()) + " / "+fmtRX.format(vnaResult.getX()));
    }
}
