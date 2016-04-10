package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.DeviceService;
import com.mindpart.radio3.device.Complex;
import javafx.event.ActionEvent;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

/**
 * Created by Robert Jaremczak
 * Date: 2016.03.24
 */
public class ComplexProbeController extends FeatureController {
    private DeviceService deviceService;
    private Label phaseNameLabel;
    private TextField phaseValueField;
    private Label phaseUnitLabel;

    public ComplexProbeController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @Override
    public void onMainButton(ActionEvent actionEvent) {
        deviceService.complexProbeGet();
    }

    @Override
    public void initialize() {
        setUpAsProbe("Complex Probe", "Gain", "dB");
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

    public void setComplex(Complex complex) {
        valueField.setText(Double.toString(complex.getValue()));
        phaseValueField.setText(Double.toString(complex.getPhase()));
    }
}
