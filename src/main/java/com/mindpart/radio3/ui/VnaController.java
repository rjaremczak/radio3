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
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.15
 */
public class VnaController implements Initializable {
    private static final long MHZ = 1000000;

    @FXML Button presetsButton;
    @FXML TextField startFrequency;
    @FXML TextField endFrequency;
    @FXML TextField numSteps;
    @FXML Button startButton;
    @FXML Label statusLabel;
    @FXML VBox vBox;
    @FXML StackPane chartsPane;
    @FXML LineChart<Double, Double> gainChart;
    @FXML NumberAxis frequencyAxis;
    @FXML NumberAxis gainAxis;
    @FXML LineChart<Double, Double> phaseChart;
    @FXML NumberAxis phaseAxis;
    @FXML ChoiceBox<String> calibrationProfile;

    private ObservableList<XYChart.Series<Double, Double>> gainChartData;
    private ObservableList<XYChart.Series<Double, Double>> phaseChartData;
    private DeviceService deviceService;

    public VnaController(Radio3 radio3) {
        this.deviceService = radio3.getDeviceService();
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initCalibrationProfiles();

        statusLabel.setText("initialized");
        gainChartData = FXCollections.observableArrayList();
        gainChart.setData(gainChartData);
        gainChart.setCreateSymbols(false);

        phaseChartData = FXCollections.observableArrayList();
        phaseChart.setData(phaseChartData);
        phaseChart.setCreateSymbols(false);

        phaseChart.setLegendVisible(false);
        phaseChart.setAnimated(false);
        phaseChart.setAlternativeRowFillVisible(false);
        phaseChart.setAlternativeColumnFillVisible(false);
        phaseChart.setHorizontalGridLinesVisible(false);
        phaseChart.setVerticalGridLinesVisible(false);
        phaseChart.getXAxis().setOpacity(0);

        phaseChart.getStylesheets().addAll(getClass().getResource("vna.css").toExternalForm());

        onPresets();
    }

    private void initCalibrationProfiles() {
        calibrationProfile.getItems().add("None");
    }

    public void doStart() {
        long fStart = (long)(Double.parseDouble(startFrequency.getText()) * MHZ);
        long fEnd = (long)(Double.parseDouble(endFrequency.getText()) * MHZ);
        int steps = Integer.parseInt(numSteps.getText());
        int fStep = (int)((fEnd - fStart) / steps);
        deviceService.startAnalyser(fStart, fStep, steps, 10, AnalyserData.Source.VNA);
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
        gainChartData.clear();
        frequencyAxis.setAutoRanging(false);
        frequencyAxis.setLowerBound(freqStartMHz);
        frequencyAxis.setUpperBound(freqEndMHz);
        frequencyAxis.setTickUnit(autoTickUnit(freqSpanMHz));
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
            gainChartData.add(chartSeries);
        }
        gainAxis.setAutoRanging(true);
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
        numSteps.setText("1000");
    }
}