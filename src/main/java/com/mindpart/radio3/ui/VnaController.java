package com.mindpart.radio3.ui;

import com.mindpart.radio3.AnalyserUnit;
import com.mindpart.radio3.device.AdcConverter;
import com.mindpart.radio3.device.AnalyserData;
import com.mindpart.radio3.device.AnalyserState;
import com.mindpart.utils.Range;
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
import java.util.function.IntToDoubleFunction;

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
    @FXML LineChart<Number, Number> swrChart;
    @FXML LineChart<Number, Number> phaseChart;
    @FXML ChoiceBox<String> calibrationProfile;

    private ObservableList<XYChart.Series<Number, Number>> gainChartData;
    private ObservableList<XYChart.Series<Number, Number>> phaseChartData;
    private AdcConverter adcConverter = AdcConverter.getDefault();
    private AnalyserUnit analyserUnit;

    public VnaController(AnalyserUnit analyserUnit) {
        this.analyserUnit = analyserUnit;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initCalibrationProfiles();

        statusLabel.setText("initialized");
        gainChartData = FXCollections.observableArrayList();
        swrChart.setData(gainChartData);
        swrChart.setCreateSymbols(false);

        phaseChartData = FXCollections.observableArrayList();
        phaseChart.setData(phaseChartData);
        phaseChart.setCreateSymbols(false);

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
        analyserUnit.startAnalyser(fStart, fStep, steps, AnalyserData.Source.VNA, this::updateData, this::updateState);
        statusLabel.setText("started");
    }

    public void updateState(AnalyserState state) {
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
        int samples[][] = ad.getData();

        updateFrequencyAxis((NumberAxis) swrChart.getXAxis(), ad.getFreqStart(), freqEnd);
        updateFrequencyAxis((NumberAxis)phaseChart.getXAxis(), ad.getFreqStart(), freqEnd);

        NumberAxis swrAxis = (NumberAxis) swrChart.getYAxis();
        swrAxis.setAutoRanging(false);
        Range swrRange = updateChart(swrChart, ad.getFreqStart(), ad.getFreqStep(), ad.getNumSteps(), samples[0], this::calculateSWR);
        swrAxis.setLowerBound(Math.min(1.0, swrRange.getMin()));
        swrAxis.setUpperBound(Math.max(2.0, swrRange.getMax()));
        swrAxis.setTickUnit(swrRange.span() < 5 ? 0.2 : (swrRange.span() < 20 ? 1.0 : 10.0));

        NumberAxis phaseAxis = (NumberAxis) phaseChart.getYAxis();
        phaseAxis.setAutoRanging(false);
        updateChart(phaseChart, ad.getFreqStart(), ad.getFreqStep(), ad.getNumSteps(), samples[1], this::calculatePhaseAngle);
        phaseAxis.setLowerBound(0);
        phaseAxis.setUpperBound(180);
        phaseAxis.setTickUnit(20);
    }

    private void updateFrequencyAxis(NumberAxis axis, long freqStart, long freqEnd) {
        double freqStartMHz = ((double)freqStart)/MHZ;
        double freqEndMHz = ((double)freqEnd)/MHZ;
        double freqSpanMHz = freqEndMHz - freqStartMHz;
        axis.setAutoRanging(false);
        axis.setLowerBound(freqStartMHz);
        axis.setUpperBound(freqEndMHz);
        axis.setTickUnit(autoTickUnit(freqSpanMHz));
    }

    private double calculateSWR(int adcValue) {
        double v = adcConverter.convert(adcValue);
        double dB = -30.0 + (v - 0.03)/0.03;
        double ratio = 1/Math.pow(10,dB/20);
        double swr = (1+ratio)/(1-ratio);
        return swr;
    }

    private double calculatePhaseAngle(int adcValue) {
        double v = adcConverter.convert(adcValue);
        double phaseDiff = (v - 0.03)/0.01;
        return phaseDiff;
    }

    private Range updateChart(LineChart<Number, Number> chart, long freqStart, long freqStep, int numSteps, int samples[], IntToDoubleFunction translate) {
        chart.getData().clear();
        XYChart.Series<Number,Number> chartSeries = new XYChart.Series<>();
        ObservableList<XYChart.Data<Number,Number>> data = chartSeries.getData();
        long freq = freqStart;
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;
        for(int num=0; num<=numSteps; num++) {
            double value = translate.applyAsDouble(samples[num]);
            minValue = Math.min(minValue, value);
            maxValue = Math.max(maxValue, value);
            XYChart.Data item = new XYChart.Data(((double)freq)/MHZ, value);
            data.add(item);
            freq += freqStep;
        }
        chart.getData().add(chartSeries);
        return new Range(minValue, maxValue);
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