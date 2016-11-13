package com.mindpart.radio3.ui;

import com.mindpart.radio3.LinearProbe;
import com.mindpart.radio3.LogarithmicProbe;
import com.mindpart.radio3.SweepProfile;
import com.mindpart.radio3.Sweeper;
import com.mindpart.radio3.device.AnalyserData;
import com.mindpart.radio3.device.AnalyserState;
import com.mindpart.types.Frequency;
import com.mindpart.types.Power;
import com.mindpart.types.Voltage;
import com.mindpart.ui.ChartMarker;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.List;
import java.util.function.Function;

import static com.mindpart.utils.FxUtils.valueFromSeries;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.15
 */
public class SweepController {
    @FXML
    AnchorPane anchorPane;

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
    LineChart<Number, Number> signalChart;

    @FXML
    NumberAxis signalAxisX;

    @FXML
    NumberAxis signalAxisY;

    private ObservableList<XYChart.Series<Number, Number>> signalDataSeries;
    private Sweeper sweeper;
    private LogarithmicProbe logarithmicProbe;
    private LinearProbe linearProbe;
    private Function<Integer, Double> probeAdcConverter;
    private SweepSettings sweepSettings;
    private ChartMarker chartMarker = new ChartMarker();
    private Function<Double, String> probeValueFormatter;

    public SweepController(Sweeper sweeper, LogarithmicProbe logarithmicProbe, LinearProbe linearProbe, List<SweepProfile> sweepProfiles) {
        this.sweeper = sweeper;
        this.logarithmicProbe = logarithmicProbe;
        this.linearProbe = linearProbe;
        this.sweepSettings = new SweepSettings(sweepProfiles);
    }

    private Frequency scenePosToFrequency(Point2D scenePos) {
        double axisX = signalAxisX.sceneToLocal(scenePos).getX();
        return Frequency.ofMHz(signalAxisX.getValueForDisplay(axisX).doubleValue());
    }

    private Point2D valueToRefPos(double value) {
        return anchorPane.sceneToLocal(signalAxisY.localToScene(0, signalAxisY.getDisplayPosition(value)));
    }

    public void initialize() throws IOException {
        initInputProbeList();

        chartMarker.initialize(anchorPane, signalChart, scenePos -> {
            Frequency freq = scenePosToFrequency(scenePos);
            double value = valueFromSeries(signalDataSeries.get(0), freq.toMHz());
            Point2D selectionPos = new Point2D(scenePos.getX(), valueToRefPos(value).getY());
            return new ChartMarker.SelectionData(selectionPos, "freq: "+freq+"\n"+probeValueFormatter.apply(value));
        });

        chartMarker.setupRangeSelection(
                data -> sweepSettings.setStartFrequency(Frequency.ofMHz(data.getXValue().doubleValue())),
                data -> sweepSettings.setEndFrequency(Frequency.ofMHz(data.getXValue().doubleValue())));

        statusLabel.setText("ready");
        signalDataSeries = FXCollections.observableArrayList();
        signalChart.setData(signalDataSeries);
        signalChart.setCreateSymbols(false);

        hBox.getChildren().add(0, sweepSettings);
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
                signalAxisY.setLabel("Power [dBm]");
                probeAdcConverter = logarithmicProbe::parse;
                probeValueFormatter = dBm -> "power: "+Power.ofDBm(dBm).formatDBm();
                break;
            }
            case LIN_PROBE: {
                signalAxisY.setLabel("Voltage [mV]");
                probeAdcConverter = linearProbe::parse;
                probeValueFormatter = mV -> "voltage: "+Voltage.ofMilliVolt(mV).format();
                break;
            }
            default:
                throw new IllegalArgumentException("not supported data source " + source);
        }
    }
    public void doStart() {
        long fStart = sweepSettings.getStartFrequency().toHz();
        long fEnd = sweepSettings.getEndFrequency().toHz();
        int steps = sweepSettings.getSteps();
        int fStep = (int) ((fEnd - fStart) / steps);
        sweeper.startAnalyser(fStart, fStep, steps, sourceProbe.getValue(), this::updateData, this::updateState);
        statusLabel.setText("started");
    }

    public void updateState(AnalyserState state) {
        statusLabel.setText(state.toString());
    }

    public void updateData(AnalyserData ad) {
        chartMarker.reset();
        int samples[] = ad.getData()[0];
        signalDataSeries.clear();

        FrequencyAxisUtils.setupFrequencyAxis(signalAxisX, ad.getFreqStart(), ad.getFreqEnd());

        XYChart.Series<Number, Number> chartSeries = new XYChart.Series<>();
        chartSeries.setName(ad.getSource().getSeriesTitle(0));
        ObservableList<XYChart.Data<Number, Number>> data = chartSeries.getData();
        long freq = ad.getFreqStart();
        for (int step = 0; step <= ad.getNumSteps(); step++) {
            XYChart.Data item = new XYChart.Data(Frequency.toMHz(freq), probeAdcConverter.apply(samples[step]));
            data.add(item);
            freq += ad.getFreqStep();
        }
        signalDataSeries.add(chartSeries);

        signalAxisY.setForceZeroInRange(false);
        signalAxisY.setAutoRanging(true);
    }
}