package com.mindpart.radio3.ui;

import com.mindpart.radio3.LinearProbe;
import com.mindpart.radio3.LogarithmicProbe;
import com.mindpart.radio3.SweepProfile;
import com.mindpart.radio3.Sweeper;
import com.mindpart.radio3.device.AnalyserData;
import com.mindpart.radio3.device.AnalyserState;
import com.mindpart.types.Frequency;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.15
 */
public class SweepGeneratorController {
    private static final long MHZ = 1000000;

    @FXML
    VBox vBox;

    @FXML
    HBox hBox;

    @FXML
    ChoiceBox<AnalyserData.Source> sourceProbe;

    @FXML
    Button startButton;

    @FXML
    Label statusLabel;

    @FXML
    LineChart<Double, Double> lineChart;

    @FXML
    NumberAxis chartAxisX;

    @FXML
    NumberAxis chartAxisY;

    private ObservableList<XYChart.Series<Double, Double>> lineChartData;
    private Sweeper sweeper;
    private LogarithmicProbe logarithmicProbe;
    private LinearProbe linearProbe;
    private Function<Integer, Double> probeAdcConverter;
    private SweepConfigControl sweepConfigControl;

    public SweepGeneratorController(Sweeper sweeper, LogarithmicProbe logarithmicProbe, LinearProbe linearProbe, List<SweepProfile> sweepProfiles) {
        this.sweeper = sweeper;
        this.logarithmicProbe = logarithmicProbe;
        this.linearProbe = linearProbe;
        this.sweepConfigControl = new SweepConfigControl(sweepProfiles);
    }

    public void initialize() throws IOException {
        initInputProbeList();

        statusLabel.setText("ready");
        lineChartData = FXCollections.observableArrayList();
        lineChart.setData(lineChartData);
        lineChart.setCreateSymbols(false);

        hBox.getChildren().add(0, sweepConfigControl);
    }

    private void initInputProbeList() {
        sourceProbe.getItems().add(AnalyserData.Source.LOG_PROBE);
        sourceProbe.getItems().add(AnalyserData.Source.LIN_PROBE);
        sourceProbe.getSelectionModel().selectFirst();
        sourceProbe.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> updateInputSource(newValue)));

        updateInputSource(sourceProbe.getValue());
    }

    private void updateInputSource(AnalyserData.Source source) {
        switch (source) {
            case LOG_PROBE: {
                chartAxisY.setLabel("Power [dBm]");
                probeAdcConverter = logarithmicProbe::parse;
                break;
            }
            case LIN_PROBE: {
                chartAxisY.setLabel("Voltage [mV]");
                probeAdcConverter = linearProbe::parse;
                break;
            }
            default:
                throw new IllegalArgumentException("not supported data source " + source);
        }
    }

    public void doStart() {
        long fStart = sweepConfigControl.getStartFrequency().toHz();
        long fEnd = sweepConfigControl.getEndFrequency().toHz();
        int steps = sweepConfigControl.getSteps();
        int fStep = (int) ((fEnd - fStart) / steps);
        sweeper.startAnalyser(fStart, fStep, steps, sourceProbe.getValue(), this::updateData, this::updateState);
        statusLabel.setText("started");
    }

    public void updateState(AnalyserState state) {
        statusLabel.setText(state.toString());
    }

    public void updateData(AnalyserData ad) {
        int samples[] = ad.getData()[0];
        lineChartData.clear();

        updateFrequencyAxis(chartAxisX, ad.getFreqStart(), ad.getFreqEnd());

        XYChart.Series<Double, Double> chartSeries = new XYChart.Series<>();
        chartSeries.setName(ad.getSource().getSeriesTitle(0));
        ObservableList<XYChart.Data<Double, Double>> data = chartSeries.getData();
        long freq = ad.getFreqStart();
        for (int step = 0; step <= ad.getNumSteps(); step++) {
            XYChart.Data item = new XYChart.Data(((double) freq) / MHZ, probeAdcConverter.apply(samples[step]));
            data.add(item);
            freq += ad.getFreqStep();
        }
        lineChartData.add(chartSeries);

        chartAxisY.setForceZeroInRange(false);
        chartAxisY.setAutoRanging(true);
    }

    private double autoTickUnit(double valueSpan) {
        for (double div = 0.000001; div <= 100; div *= 10) {
            if (valueSpan < div) {
                return div / 25;
            }
        }
        return 1.0;
    }

    private void updateFrequencyAxis(NumberAxis axis, long freqStart, long freqEnd) {
        double freqStartMHz = ((double) freqStart) / MHZ;
        double freqEndMHz = ((double) freqEnd) / MHZ;
        double freqSpanMHz = freqEndMHz - freqStartMHz;
        axis.setAutoRanging(false);
        axis.setLowerBound(freqStartMHz);
        axis.setUpperBound(freqEndMHz);
        axis.setTickUnit(autoTickUnit(freqSpanMHz));
    }
}