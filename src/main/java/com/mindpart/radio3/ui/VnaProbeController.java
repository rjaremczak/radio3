package com.mindpart.radio3.ui;

import com.mindpart.radio3.VnaProbe;
import com.mindpart.radio3.device.Complex;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class VnaProbeController extends FeatureController {
    private static final NumberFormat fmtGain = new DecimalFormat("0.000");
    private static final NumberFormat fmtPhase = new DecimalFormat("0.000");

    private Label phaseNameLabel;
    private TextField phaseValueField;
    private Label phaseUnitLabel;
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
        setUpAsProbe("VNA Probe", "SWR", "");
        phaseNameLabel = new Label("Phase");
        phaseUnitLabel = new Label("°");

        phaseValueField = new TextField();
        phaseValueField.setAlignment(valueField.getAlignment());

        gridPane.getChildren().remove(buttonBox);
        gridPane.add(phaseNameLabel, 0, 1);
        gridPane.add(phaseValueField, 1, 1, 2, 1);
        gridPane.add(phaseUnitLabel, 3, 1);
        gridPane.add(buttonBox, 1, 2, Integer.MAX_VALUE, 1);
    }

    public void update(Complex complex) {
        valueField.setText(fmtGain.format(complex.getValue()));
        phaseValueField.setText(fmtPhase.format(complex.getPhase()));
    }
}
