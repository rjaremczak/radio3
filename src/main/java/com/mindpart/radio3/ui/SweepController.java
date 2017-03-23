package com.mindpart.radio3.ui;

import com.mindpart.radio3.LinearParser;
import com.mindpart.radio3.LogarithmicParser;
import com.mindpart.radio3.SweepProfile;
import com.mindpart.radio3.device.AnalyserData;
import com.mindpart.radio3.device.AnalyserDataInfo;
import com.mindpart.radio3.device.AnalyserDataSource;
import com.mindpart.radio3.device.AnalyserState;
import com.mindpart.types.Frequency;
import com.mindpart.types.Power;
import com.mindpart.types.Voltage;
import com.mindpart.ui.ChartMarker;
import com.mindpart.utils.FxUtils;
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
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Consumer;
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
    ToggleButton btnNormalize;

    @FXML
    Button btnOnce;

    @FXML
    ToggleButton btnContinuous;

    @FXML
    LineChart<Number, Number> signalChart;

    @FXML
    NumberAxis signalAxisX;

    @FXML
    NumberAxis signalAxisY;

    private Radio3 radio3;
    private ObservableList<XYChart.Series<Number, Number>> signalDataSeries;
    private LogarithmicParser logarithmicParser;
    private LinearParser linearParser;
    private Function<Integer, Double> probeAdcConverter;
    private SweepSettingsPane sweepSettingsPane;
    private ChartMarker chartMarker = new ChartMarker();
    private Function<Double, String> probeValueFormatter;
    private BiFunction<Integer, Double, Double> valueProcessor = this::originalValue;
    private List<XYChart.Data<Double, Double>> receivedData = new ArrayList<>();
    private List<Double> referenceData = new ArrayList<>();
    private AnalyserDataInfo receivedDataInfo;
    private MainController mainController;

    public SweepController(Radio3 radio3, MainController mainController, LogarithmicParser logarithmicParser, LinearParser linearParser, List<SweepProfile> sweepProfiles) {
        this.radio3 = radio3;
        this.mainController = mainController;
        this.logarithmicParser = logarithmicParser;
        this.linearParser = linearParser;
        this.sweepSettingsPane = new SweepSettingsPane(sweepProfiles);
    }

    private Frequency scenePosToFrequency(Point2D scenePos) {
        double axisX = signalAxisX.sceneToLocal(scenePos).getX();
        return Frequency.ofMHz(signalAxisX.getValueForDisplay(axisX).doubleValue());
    }

    private Point2D valueToRefPos(double value) {
        return anchorPane.sceneToLocal(signalAxisY.localToScene(0, signalAxisY.getDisplayPosition(value)));
    }

    private Double originalValue(int index, Double value) {
        return value;
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
        }, () -> !btnContinuous.isSelected());

        chartMarker.setupRangeSelection(
                data -> sweepSettingsPane.setStartFrequency(Frequency.ofMHz(data.getXValue().doubleValue())),
                data -> sweepSettingsPane.setEndFrequency(Frequency.ofMHz(data.getXValue().doubleValue())));

        signalDataSeries = FXCollections.observableArrayList();
        signalChart.setData(signalDataSeries);
        signalChart.setCreateSymbols(false);

        hBox.getChildren().add(0, sweepSettingsPane);

        btnNormalize.selectedProperty().addListener(this::onNormalizeChanged);
        btnContinuous.selectedProperty().addListener(this::onContinuousChanged);
    }

    private void onNormalizeChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean normalized) {
        if(receivedData.isEmpty()) {
            btnNormalize.setSelected(false);
            return;
        }

        sweepSettingsPane.disableControls(normalized);
        sourceProbe.setDisable(normalized);
        if(normalized) {
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
                valueProcessor = this::originalValue;
                break;
            }
            case LIN_PROBE: {
                signalAxisY.setLabel("Voltage [mV]");
                probeAdcConverter = linearParser::parse;
                probeValueFormatter = this::voltageValueFormatter;
                valueProcessor = this::originalValue;
                break;
            }
            default:
                throw new IllegalArgumentException("not supported data source " + source);
        }
    }

    public void onSweepOnce() {
        btnNormalize.setDisable(true);
        sweepOnce(sweepSettingsPane.getQuality(), (analyserData) -> {
            updateAnalyserData(analyserData);
            btnNormalize.setDisable(false);
        });
        mainController.setDeviceStatus(AnalyserState.PROCESSING);
    }

    private void onContinuousChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean continuous) {
        if(continuous) {
            FxUtils.disableItems(btnOnce, btnNormalize, sourceProbe);
            sweepSettingsPane.disableControls(true);
            mainController.disableAllExcept(true, mainController.sweepTab);
            sweepOnce(sweepSettingsPane.getQuality(), this::displayDataAndSweepAgain);
            btnContinuous.setText("Stop");
        } else {
            if(btnNormalize.isSelected()) {
                FxUtils.enableItems(btnOnce, btnNormalize);
            } else {
                FxUtils.enableItems(btnOnce, btnNormalize, sourceProbe);
                sweepSettingsPane.disableControls(false);
            }
            mainController.disableAllExcept(false, mainController.sweepTab);
            btnContinuous.setText("Continuous");
        }
    }

    private void displayDataAndSweepAgain(AnalyserData analyserData) {
        if(btnContinuous.isSelected()) {
            updateAnalyserData(analyserData);
            sweepOnce(sweepSettingsPane.getQuality(), this::displayDataAndSweepAgain);
        }
    }

    private void sweepOnce(SweepQuality quality, Consumer<AnalyserData> dataHandler) {
        long fStart = sweepSettingsPane.getStartFrequency().toHz();
        long fEnd = sweepSettingsPane.getEndFrequency().toHz();
        int fStep = (int) ((fEnd - fStart) / quality.getSteps());
        radio3.startAnalyser(fStart, fStep, quality, sourceProbe.getValue(), dataHandler);
    }

    public void updateAnalyserData(AnalyserData ad) {
        receivedDataInfo = ad.toInfo();
        receivedData.clear();

        int samples[] = ad.getData()[0];
        long freq = ad.getFreqStart();
        for (int step = 0; step <= ad.getNumSteps(); step++) {
            receivedData.add(new XYChart.Data<>(Frequency.toMHz(freq), probeAdcConverter.apply(samples[step])));
            freq += ad.getFreqStep();
        }

        updateChart();
    }

    private void updateChart() {
        clear();

        if(receivedDataInfo==null) { return; }

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
    }

    void clear() {
        chartMarker.clear();
        signalDataSeries.clear();
    }
}