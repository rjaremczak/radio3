package com.mindpart.radio3.ui;

import com.mindpart.radio3.LinearParser;
import com.mindpart.radio3.LogarithmicParser;
import com.mindpart.radio3.SweepProfile;
import com.mindpart.radio3.Sweeper;
import com.mindpart.radio3.device.AnalyserData;
import com.mindpart.radio3.device.AnalyserDataInfo;
import com.mindpart.radio3.device.AnalyserDataSource;
import com.mindpart.radio3.device.AnalyserState;
import com.mindpart.types.Frequency;
import com.mindpart.types.Power;
import com.mindpart.types.Voltage;
import com.mindpart.ui.ChartMarker;
import javafx.beans.value.ObservableValue;
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
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import org.apache.commons.math3.util.Precision;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    ChoiceBox<AnalyserDataSource> sourceProbe;

    @FXML
    ToggleButton btnCalibrate;

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
    private LogarithmicParser logarithmicParser;
    private LinearParser linearParser;
    private Function<Integer, Double> probeAdcConverter;
    private SweepSettings sweepSettings;
    private ChartMarker chartMarker = new ChartMarker();
    private Function<Double, String> probeValueFormatter;
    private BiFunction<Integer, Double, Double> valueProcessor = (index, value) -> Precision.round(value,1);
    private List<XYChart.Data<Double, Double>> receivedData = new ArrayList<>();
    private List<Double> referenceData = new ArrayList<>();
    private AnalyserDataInfo receivedDataInfo;

    public SweepController(Sweeper sweeper, LogarithmicParser logarithmicParser, LinearParser linearParser, List<SweepProfile> sweepProfiles) {
        this.sweeper = sweeper;
        this.logarithmicParser = logarithmicParser;
        this.linearParser = linearParser;
        this.sweepSettings = new SweepSettings(sweepProfiles);
    }

    private Frequency scenePosToFrequency(Point2D scenePos) {
        double axisX = signalAxisX.sceneToLocal(scenePos).getX();
        return Frequency.ofMHz(signalAxisX.getValueForDisplay(axisX).doubleValue());
    }

    private Point2D valueToRefPos(double value) {
        return anchorPane.sceneToLocal(signalAxisY.localToScene(0, signalAxisY.getDisplayPosition(value)));
    }

    private Double fromLogarithmicToRelativeGain(int index, Double value) {
        return value - referenceData.get(index);
    }

    private Double fromLinearTo1mV(int index, Double value) {
        return 1.0 + (value - referenceData.get(index));
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

        btnCalibrate.selectedProperty().addListener(this::onNormalizeChanged);
    }

    private void onNormalizeChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean relative) {
        sweepSettings.setEditable(!relative);
        sourceProbe.setDisable(relative);
        if(relative) {
            referenceData = receivedData.stream().map(XYChart.Data::getYValue).collect(Collectors.toList());
            if(sourceProbe.getValue() == AnalyserDataSource.LOG_PROBE) {
                signalAxisY.setLabel("Normalized Power [dBm]");
                probeAdcConverter = logarithmicParser::parse;
                probeValueFormatter = this::powerValueFormatter;
                valueProcessor = this::fromLogarithmicToRelativeGain;
            } else {
                signalAxisY.setLabel("Normalized Voltage [mV]");
                probeAdcConverter = linearParser::parse;
                probeValueFormatter = this::voltageValueFormatter;
                valueProcessor = this::fromLinearTo1mV;
            }
        } else {
            referenceData.clear();
            updateInputSource(sourceProbe.getValue());
        }

        updateChart();
    }

    private void initInputProbeList() {
        sourceProbe.getItems().add(AnalyserDataSource.LOG_PROBE);
        sourceProbe.getItems().add(AnalyserDataSource.LIN_PROBE);
        sourceProbe.getSelectionModel().selectFirst();
        sourceProbe.getSelectionModel().selectedItemProperty().addListener(((observable, oldValue, newValue) -> updateInputSource(newValue)));

        updateInputSource(sourceProbe.getValue());
    }

    private String powerValueFormatter(double dBm) {
        return "power: "+Power.ofDBm(dBm).formatDBm();
    }

    private String voltageValueFormatter(double mV) {
        return "voltage: "+Voltage.ofMilliVolt(mV).format();
    }

    private void updateInputSource(AnalyserDataSource source) {
        switch (source) {
            case LOG_PROBE: {
                signalAxisY.setLabel("Power [dBm]");
                probeAdcConverter = logarithmicParser::parse;
                probeValueFormatter = this::powerValueFormatter;
                valueProcessor = (index, value) -> Precision.round(value, 1);
                break;
            }
            case LIN_PROBE: {
                signalAxisY.setLabel("Voltage [mV]");
                probeAdcConverter = linearParser::parse;
                probeValueFormatter = this::voltageValueFormatter;
                valueProcessor = (index, value) -> Precision.round(value, 1);
                break;
            }
            default:
                throw new IllegalArgumentException("not supported data source " + source);
        }
    }

    public void doStart() {
        btnCalibrate.setDisable(true);
        long fStart = sweepSettings.getStartFrequency().toHz();
        long fEnd = sweepSettings.getEndFrequency().toHz();
        int steps = sweepSettings.getSteps();
        int fStep = (int) ((fEnd - fStart) / steps);
        sweeper.startAnalyser(fStart, fStep, steps, sourceProbe.getValue(), this::updateAnalyserData);
        statusLabel.setText(AnalyserState.PROCESSING.toString());
    }

    public void updateAnalyserState(AnalyserState state) {
        statusLabel.setText(state.toString());
    }

    public void updateAnalyserData(AnalyserData ad) {
        receivedDataInfo = ad.toInfo();
        receivedData.clear();

        int samples[] = ad.getData()[0];
        long freq = ad.getFreqStart();
        for (int step = 0; step <= ad.getNumSteps(); step++) {
            receivedData.add(new XYChart.Data<>(Precision.round(Frequency.toMHz(freq),1), probeAdcConverter.apply(samples[step])));
            freq += ad.getFreqStep();
        }

        updateChart();
    }

    private void updateChart() {
        chartMarker.reset();
        signalDataSeries.clear();

        XYChart.Series<Number, Number> chartSeries = new XYChart.Series<>();
        chartSeries.setName(receivedDataInfo.getSource().getSeriesTitle(0));
        ObservableList<XYChart.Data<Number, Number>> data = chartSeries.getData();

        for (int step = 0; step < receivedData.size(); step++) {
            XYChart.Data<Double, Double> received = receivedData.get(step);
            data.add(new XYChart.Data<>(received.getXValue(), valueProcessor.apply(step, received.getYValue())));
        }

        signalDataSeries.add(chartSeries);
        signalAxisX.setForceZeroInRange(false);
        FrequencyAxisUtils.setupFrequencyAxis(signalAxisX, receivedDataInfo.getFreqStart(), receivedDataInfo.getFreqEnd());

        signalAxisY.setAutoRanging(true);
        signalAxisY.setForceZeroInRange(false);
        btnCalibrate.setDisable(false);
    }
}