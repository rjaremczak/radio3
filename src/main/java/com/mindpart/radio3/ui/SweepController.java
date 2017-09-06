package com.mindpart.radio3.ui;

import com.mindpart.discrete.Range;
import com.mindpart.radio3.device.*;
import com.mindpart.types.Frequency;
import com.mindpart.ui.ChartMarker;
import com.mindpart.ui.FxUtils;
import javafx.application.Platform;
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
import java.util.function.Consumer;

import static com.mindpart.ui.FxChartUtils.rangeAxis;
import static com.mindpart.ui.FxUtils.valueFromSeries;

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
    HBox controlBox;

    @FXML
    HBox chartBox;

    @FXML
    ChoiceBox<SweepSignalSource> sourceProbe;

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
    private SweepSettingsController sweepSettingsController;
    private ChartMarker chartMarker = new ChartMarker();
    private MainController mainController;
    private ChartContext<Integer,Double> chartContext;
    private SweepInfoController sweepInfoController;

    private SweepDataInfo receivedDataInfo;
    private double[] receivedData;
    private double[] processedData;
    private double[] receivedFreq;
    
    public SweepController(Radio3 radio3, MainController mainController) {
        this.radio3 = radio3;
        this.mainController = mainController;
        this.sweepSettingsController = new SweepSettingsController(mainController.bundle, radio3.getSweepProfiles());
    }

    private void initChartContext() {
        boolean normalized = btnNormalize.isSelected();
        SweepSignalSource signalSource = sourceProbe.getValue();

        if(signalSource == SweepSignalSource.LOG_PROBE) {
            if(normalized) {
                chartContext = new LogarithmicProbeNormContext(radio3.getLogarithmicParser()::parse, receivedData, mainController.bundle.axisRelativePower);
            } else {
                chartContext = new LogarithmicProbeContext(radio3.getLogarithmicParser()::parse, mainController.bundle.axisPower);
            }
        } else if(signalSource == SweepSignalSource.LIN_PROBE) {
            if(normalized) {
                chartContext = new LinearProbeNormContext(radio3.getLinearParser()::parse, receivedData, mainController.bundle.axisRelativeVoltage);
            } else {
                chartContext = new LinearProbeContext(radio3.getLinearParser()::parse, mainController.bundle.axisVoltage);
            }
        } else {
            throw new IllegalStateException("source probe: "+sourceProbe.getValue());
        }
        
        signalAxisY.setLabel(chartContext.axisLabel());
    }

    private Frequency scenePosToFrequency(Point2D scenePos) {
        double axisX = signalAxisX.sceneToLocal(scenePos).getX();
        return Frequency.ofMHz(signalAxisX.getValueForDisplay(axisX).doubleValue());
    }

    private Point2D valueToRefPos(double value) {
        return anchorPane.sceneToLocal(signalAxisY.localToScene(0, signalAxisY.getDisplayPosition(value)));
    }

    public void initialize() throws IOException {
        chartMarker.initialize(anchorPane, signalChart, scenePos -> {
            if(signalDataSeries.isEmpty()) return null;
            
            Frequency freq = scenePosToFrequency(scenePos);
            double value = valueFromSeries(signalDataSeries.get(0), freq.toMHz());
            Point2D selectionPos = new Point2D(scenePos.getX(), valueToRefPos(value).getY());
            return new ChartMarker.SelectionData(selectionPos, "f = "+freq+"\n" + chartContext.valueLabel()+" = "+ chartContext.format(value));
        }, () -> !btnContinuous.isSelected());

        chartMarker.setupRangeSelection(
                data -> sweepSettingsController.setStartFrequency(Frequency.ofMHz(data.getXValue().doubleValue())),
                data -> sweepSettingsController.setEndFrequency(Frequency.ofMHz(data.getXValue().doubleValue())));

        signalDataSeries = FXCollections.observableArrayList();
        signalChart.setData(signalDataSeries);
        signalChart.setCreateSymbols(false);

        controlBox.getChildren().add(0, mainController.loadFXml(sweepSettingsController, "sweepSettingsPane.fxml"));

        btnNormalize.selectedProperty().addListener(this::normalizeChangeListener);
        btnContinuous.selectedProperty().addListener(this::continuousChangeListener);

        sweepSettingsController.setRangeChangeListener(this::sweepSettingsChangeListener);
        sweepSettingsController.setQualityChangeListener(this::sweepSettingsChangeListener);

        initInputProbeList();
        initChartContext();

        sweepInfoController = new SweepInfoController(mainController, anchorPane, signalChart);
        chartBox.getChildren().add(sweepInfoController.getContainer());

        updateNormButton();
    }

    private void sweepSettingsChangeListener() {
        clear();
        updateNormButton();
    }

    private final boolean receivedDataReady() {
        return receivedData != null && receivedFreq != null;
    }

    private void normalizeChangeListener(ObservableValue<? extends Boolean> ob, Boolean ov, Boolean normalized) {
        if(!receivedDataReady()) {
            btnNormalize.setSelected(false);
            return;
        }

        sweepSettingsController.disableControls(normalized);
        sourceProbe.setDisable(normalized);
        initChartContext();
        updateChart();
    }

    private void inputSourceChangeListener(ObservableValue<? extends SweepSignalSource> ob, SweepSignalSource old, SweepSignalSource source) {
        clear();
        initChartContext();
        updateNormButton();
        sweepInfoController.clear();
    }

    private void initInputProbeList() {
        sourceProbe.getItems().add(SweepSignalSource.LOG_PROBE);
        sourceProbe.getItems().add(SweepSignalSource.LIN_PROBE);
        sourceProbe.getSelectionModel().selectFirst();
        sourceProbe.getSelectionModel().selectedItemProperty().addListener(this::inputSourceChangeListener);
    }

    private void disableUI() {
        FxUtils.disableItems(btnOnce, btnNormalize, sourceProbe);
        sweepSettingsController.disableControls(true);
        mainController.disableAllExcept(true, mainController.sweepTab);
        if(!btnContinuous.isSelected()) btnContinuous.setDisable(true);
    }

    private void enableUI() {
        if(!btnContinuous.isSelected()) btnContinuous.setDisable(false);
        if(btnNormalize.isSelected()) {
            FxUtils.enableItems(btnOnce, btnNormalize);
        } else {
            FxUtils.enableItems(btnOnce, btnNormalize, sourceProbe);
            sweepSettingsController.disableControls(false);
        }
        mainController.disableAllExcept(false, mainController.sweepTab);
        mainController.requestDeviceState();
    }

    public void onSweepOnce() {
        disableUI();
        mainController.updateDeviceStatus(DeviceStatus.PROCESSING);
        runSweepOnce(analyserResponse -> {
            updateAnalyserData(analyserResponse);
            enableUI();
        });
    }

    private void displayDataAndSweepAgain(SweepResponse analyserResponse) {
        if(btnContinuous.isSelected()) {
            updateAnalyserData(analyserResponse);
            runSweepOnce(this::displayDataAndSweepAgain);
        }
    }

    public void updateAnalyserData(SweepResponse ad) {
        receivedDataInfo = ad.toInfo();
        int samples[] = ad.getData()[0];
        long freq = ad.getFreqStart();
        initReceivedData(samples.length);
        for (int step = 0; step <= ad.getNumSteps(); step++) {
            receivedData[step] = chartContext.parse(samples[step]);
            receivedFreq[step] = Frequency.toMHz(freq);
            freq += ad.getFreqStep();
        }

        updateChart();
    }
    
    private void runSweepOnce(Consumer<SweepResponse> analyserDataConsumer) {
        radio3.executeInBackground(() -> {
            Response<SweepResponse> response = sweepOnce();
            if(response.isOK() && radio3.isConnected()) {
                Platform.runLater(() -> analyserDataConsumer.accept(response.getData()));
            } else {
                enableUI();
            }
        });
    }

    private void continuousChangeListener(ObservableValue<? extends Boolean> ob, Boolean ov, Boolean continuous) {
        if(continuous) {
            disableUI();
            runSweepOnce(this::displayDataAndSweepAgain);
            btnContinuous.setText(mainController.bundle.buttonStop);
        } else {
            enableUI();
            btnContinuous.setText(mainController.bundle.buttonContinuous);
        }
    }

    private Response<SweepResponse> sweepOnce() {
        SweepQuality quality = sweepSettingsController.getQuality();
        long fStart = sweepSettingsController.getStartFrequency().toHz();
        long fEnd = sweepSettingsController.getEndFrequency().toHz();
        int fStep = (int) ((fEnd - fStart) / quality.getSteps());
        return radio3.startAnalyser(fStart, fStep,
                quality.getSteps(), quality.getAvgPasses(), quality.getAvgSamples(), sourceProbe.getValue());
    }

    private void updateNormButton() {
        btnNormalize.setDisable(receivedDataReady());
    }

    private void updateChart() {
        chartMarker.clear();
        signalDataSeries.clear();
        sweepInfoController.clear();

        if(receivedDataInfo==null) { return; }

        XYChart.Series<Number, Number> chartSeries = new XYChart.Series<>();
        chartSeries.setName(receivedDataInfo.getSource().getSeriesTitle(0));
        ObservableList<XYChart.Data<Number, Number>> data = chartSeries.getData();

        Range range = new Range();
        for (int step = 0; step < receivedData.length; step++) {
            double processed = range.sample(chartContext.process(step, receivedData[step]));
            processedData[step] = processed;
            data.add(new XYChart.Data<>(receivedFreq[step], processed));
        }

        signalDataSeries.add(chartSeries);
        signalAxisX.setForceZeroInRange(false);
        FrequencyAxisUtils.setupFrequencyAxis(signalAxisX, receivedDataInfo.getFreqStart(), receivedDataInfo.getFreqEnd());
        switch (sourceProbe.getValue()) {
            case LIN_PROBE:
                rangeAxis(signalAxisY, range, 0.02, 0.01);
                break;
            default:
                rangeAxis(signalAxisY, range, 6, 1);
        }

        sweepInfoController.update(receivedFreq, processedData, i -> Frequency.ofMHz(receivedFreq[i]).format(), chartContext);
    }

    private void initReceivedData(int length) {
        receivedData = new double[length];
        receivedFreq = new double[length];
        processedData = new double[length];
    }

    private void clearReceivedData() {
        receivedFreq = null;
        receivedData = null;
        processedData = null;
    }

    void clear() {
        chartMarker.clear();
        signalDataSeries.clear();
        sweepInfoController.clear();
        clearReceivedData();
    }
}