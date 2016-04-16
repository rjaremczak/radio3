package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.AnalyserStatus;
import com.mindpart.radio3.device.DeviceService;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.15
 */
public class AnalyserController implements Initializable {
    @FXML Button presetsButton;
    @FXML TextField startFrequency;
    @FXML TextField endFrequency;
    @FXML TextField frequencyStep;
    @FXML Button startButton;
    @FXML Label statusLabel;
    @FXML LineChart<Number, Number> lineChart;

    private DeviceService deviceService;

    public AnalyserController(Radio3 radio3) {
        this.deviceService = radio3.getDeviceService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusLabel.setText("initialized");
        onPresets();
    }

    public void doStart() {
        long fStart = Long.parseLong(startFrequency.getText());
        long fEnd = Long.parseLong(endFrequency.getText());
        long fStep = Long.parseLong(frequencyStep.getText());
        int numSteps = (int) (((fStart - fEnd) / fStep) + 1);
        deviceService.startAnalyser(fStart, fStep, numSteps, 10);
        statusLabel.setText("started");
    }

    public void updateStatus(AnalyserStatus status) {
        statusLabel.setText(status.getState().name());
    }

    public void onStartFrequency() {

    }

    public void onEndFrequency() {

    }

    public void onFrequencyStep() {

    }

    public void onPresets() {
        startFrequency.setText("3500000");
        endFrequency.setText("3800000");
        frequencyStep.setText("10000");
    }
}