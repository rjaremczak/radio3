package com.mindpart.radio3.ui;

import com.mindpart.radio3.device.AnalyserData;
import com.mindpart.radio3.device.AnalyserState;
import com.mindpart.radio3.device.DeviceService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
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
    private static final long MHZ = 1000000;

    @FXML Button presetsButton;
    @FXML TextField startFrequency;
    @FXML TextField endFrequency;
    @FXML TextField numSteps;
    @FXML ChoiceBox<AnalyserData.Source> sourceProbe;
    @FXML Button startButton;
    @FXML Label statusLabel;
    @FXML VBox analyserVBox;
    @FXML LineChart<Double, Double> lineChart;
    @FXML NumberAxis chartAxisX;
    @FXML NumberAxis chartAxisY;

    private ObservableList<XYChart.Series<Double, Double>> lineChartData;
    private DeviceService deviceService;

    public AnalyserController(Radio3 radio3) {
        this.deviceService = radio3.getDeviceService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initInputProbeList();

        statusLabel.setText("initialized");
        lineChartData = FXCollections.observableArrayList();
        lineChart.setData(lineChartData);
        lineChart.setCreateSymbols(false);
        onPresets();
    }

    private void initInputProbeList() {
        sourceProbe.getItems().addAll(AnalyserData.Source.values());
        sourceProbe.getSelectionModel().selectFirst();
    }

    public void doStart() {
        long fStart = (long)(Double.parseDouble(startFrequency.getText()) * MHZ);
        long fEnd = (long)(Double.parseDouble(endFrequency.getText()) * MHZ);
        int steps = Integer.parseInt(numSteps.getText());
        int fStep = (int)((fEnd - fStart) / steps);
        deviceService.startAnalyser(fStart, fStep, steps, 10, sourceProbe.getValue());
        statusLabel.setText("started");
    }

    public void updateStatus(AnalyserState state) {
        statusLabel.setText(state.toString());
    }

    private double autoTickUnit(double valueSpan) {
        for(double div=0.000001; div<10; div*=10) {
            if(valueSpan < div) {
                return div/10;
            }
        }
        return 1.0;
    }

    public void updateData(AnalyserData ad) {
        long freqEnd = ad.getFreqStart() + (ad.getNumSteps() * ad.getFreqStep());
        double freqStartMHz = ((double)ad.getFreqStart())/MHZ;
        double freqEndMHz = ((double)freqEnd)/MHZ;
        double freqSpanMHz = freqEndMHz - freqStartMHz;
        int samples[][] = ad.getData();
        lineChartData.clear();
        chartAxisX.setAutoRanging(false);
        chartAxisX.setLowerBound(freqStartMHz);
        chartAxisX.setUpperBound(freqEndMHz);
        chartAxisX.setTickUnit(autoTickUnit(freqSpanMHz));
        for(int series=0; series<ad.getNumSeries(); series++) {
            XYChart.Series<Double,Double> chartSeries = new XYChart.Series<>();
            chartSeries.setName(ad.getSource().getSeriesTitle(series));
            ObservableList<XYChart.Data<Double,Double>> data = chartSeries.getData();
            long freq = ad.getFreqStart();
            for(int step=0; step<=ad.getNumSteps(); step++) {
                XYChart.Data item = new XYChart.Data(((double)freq)/MHZ, samples[series][step]);
                data.add(item);
                freq += ad.getFreqStep();
            }
            lineChartData.add(chartSeries);
        }
        chartAxisY.setAutoRanging(true);
    }

    public void onStartFrequency() {

    }

    public void onEndFrequency() {

    }

    public void onNumSteps() {

    }

    public void onPresets() {
        startFrequency.setText("1.8");
        endFrequency.setText("60.0");
        numSteps.setText("500");
    }
}