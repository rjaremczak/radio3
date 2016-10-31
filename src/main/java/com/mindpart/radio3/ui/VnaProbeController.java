package com.mindpart.radio3.ui;

import com.mindpart.radio3.VnaProbe;
import com.mindpart.radio3.device.Complex;
import javafx.event.ActionEvent;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class VnaProbeController extends ComponentController {
    private static final NumberFormat fmtGain = new DecimalFormat("0.000");
    private static final NumberFormat fmtPhase = new DecimalFormat("0.000");

    private VnaProbe vnaProbe;

    public VnaProbeController(VnaProbe vnaProbe) {
        this.vnaProbe = vnaProbe;
    }

    @Override
    public void onMainButton(ActionEvent actionEvent) {
        vnaProbe.requestData();
    }

    @Override
    public void initialize() {
        setUpAsProbe("VNA Probe", "SWR / Phase");
    }

    public void update(Complex complex) {
        valueField.setText(fmtGain.format(complex.getValue())+" / "+fmtPhase.format(complex.getPhase())+" °");
    }
}
