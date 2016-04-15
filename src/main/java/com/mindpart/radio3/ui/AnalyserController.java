package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.AnalyserStatus;
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

    @FXML private Button presetsButton;
    @FXML private TextField startFrequency;
    @FXML private TextField endFrequency;
    @FXML private TextField frequencyStep;
    @FXML private Button startButton;
    @FXML private Label statusLabel;
    @FXML private LineChart<Number, Number> lineChart;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusLabel.setText("initialized");
    }

    public void doStart() {
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

    }
}