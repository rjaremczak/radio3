package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.AnalyserData;
import com.mindpart.radio3.device.AnalyserStatus;
import com.mindpart.radio3.device.DeviceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;

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
    @FXML TextField numSteps;
    @FXML Button startButton;
    @FXML Label statusLabel;
    @FXML VBox analyserVBox;
    @FXML LineChart<Double, Double> lineChart;

    private ObservableList<XYChart.Series<Double, Double>> lineChartData;
    private DeviceService deviceService;

    public AnalyserController(Radio3 radio3) {
        this.deviceService = radio3.getDeviceService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        statusLabel.setText("initialized");
        lineChartData = FXCollections.observableArrayList();
        lineChart.setData(lineChartData);
        onPresets();
    }

    public void doStart() {
        long fStart = Long.parseLong(startFrequency.getText());
        long fEnd = Long.parseLong(endFrequency.getText());
        int steps = Integer.parseInt(numSteps.getText());
        int fStep = (int)((fEnd - fStart) / steps);
        deviceService.startAnalyser(fStart, fStep, steps, 10);
        statusLabel.setText("started");
    }

    public void updateStatus(AnalyserStatus status) {
        statusLabel.setText(status.getState().name());

        lineChartData.clear();
        XYChart.Series<Double, Double> series = new XYChart.Series<>();
        series.setName("sweep range from "+startFrequency.getText()+" Hz to "+endFrequency.getText()+" Hz");

        ObservableList<XYChart.Data<Double, Double>> data = series.getData();

        data.add(new XYChart.Data(30000000L, 0.123));
        data.add(new XYChart.Data(35000000L, 0.223));

        lineChartData.add(series);
    }

    public void updateData(AnalyserData analyserData) {

    }

    public void onStartFrequency() {

    }

    public void onEndFrequency() {

    }

    public void onNumSteps() {

    }

    public void onPresets() {
        startFrequency.setText("3500000");
        endFrequency.setText("3800000");
        numSteps.setText("1000");
    }
}