package com.mindpart.radio3.ui;

import com.mindpart.radio3.VnaResult;
import com.mindpart.radio3.device.Radio3;
import com.mindpart.radio3.device.Response;
import javafx.event.ActionEvent;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class VnaProbeController extends ComponentController {
    private static final NumberFormat FORMAT_SWR = new DecimalFormat("0.0");
    private static final NumberFormat FORMAT_RX = new DecimalFormat("0.0");

    private Radio3 radio3;

    public VnaProbeController(Radio3 radio3) {
        this.radio3 = radio3;
    }

    @Override
    public void onMainButton(ActionEvent actionEvent) {
        Response<VnaResult> response = radio3.readVnaProbe();
        if(response.isOK()) update(response.getData());
    }

    @Override
    public void initialize() {
        setUpAsProbe("VNA Probe", "Impedance");
    }

    public void update(VnaResult vnaResult) {
        valueField.setText(FORMAT_RX.format(vnaResult.getR())+" + j"+FORMAT_RX.format(vnaResult.getX())+" Ω");
    }
}
