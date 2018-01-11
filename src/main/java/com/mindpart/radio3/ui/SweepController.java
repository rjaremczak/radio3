package com.mindpart.radio3.ui;

import com.mindpart.javafx.EnhancedLineChart;
import com.mindpart.numeric.Range;
import com.mindpart.radio3.device.*;
import com.mindpart.science.Frequency;
import com.mindpart.ui.ChartMarker;
import com.mindpart.ui.FxChartUtils;
import com.mindpart.ui.FxUtils;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.chart.XYChart.Data;
import javafx.scene.control.Accordion;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;

import static com.mindpart.radio3.device.SweepSignalSource.LIN_PROBE;
import static com.mindpart.radio3.device.SweepSignalSource.LOG_PROBE;
import static com.mindpart.science.UnitPrefix.MEGA;
import static com.mindpart.ui.FxUtils.valueFromSeries;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.15
 */
public class SweepController extends AbstractSweepController {
    @FXML
    private AnchorPane anchorPane;

    @FXML
    private HBox controlBox;

    @FXML
    private HBox chartBox;

    @FXML
    private ChoiceBox<SweepSignalSource> sourceProbe;

    @FXML
    private ToggleButton btnNormalize;

    @FXML
    private ToggleButton btnTools;

    @FXML
    private Button btnZoomIn;

    @FXML
    private Button btnZoomOut;

    @FXML
    private Button btnPanUp;

    @FXML
    private Button btnPanDown;

    @FXML
    private Button btnAutoRange;

    private final ChartMarker chartMarker = new ChartMarker();
    private final MainController mainController;
    private final RangeToolController rangeToolController;
    private final ChartContext chartContext = new ChartContext();

    private ObservableList<XYChart.Series<Number, Number>> signalDataSeries;

    private FilterToolController filterToolController;
    private VBox chartTools;
    private Accordion chartToolsAccordion;
    private SweepDataInfo receivedDataInfo;
    private EnhancedLineChart<Number, Number> signalChart;
    private NumberAxis signalAxisX;
    private NumberAxis signalAxisY;

    public SweepController(Radio3 radio3, MainController mainController) {
        super(radio3, mainController.ui);
        this.mainController = mainController;

        rangeToolController = new RangeToolController(ui, chartContext);
    }

    private void initChartContext() {
        boolean normalized = btnNormalize.isSelected();
        SweepSignalSource signalSource = sourceProbe.getValue();

        if(signalSource == LOG_PROBE) {
            if(normalized) {
                chartContext.valueProcessor = new LogProbeNormProcessor(radio3.getLogarithmicParser()::parse, chartContext.receivedData, ui.text("axis.relativePower"));
            } else {
                chartContext.valueProcessor = new LogProbeProcessor(radio3.getLogarithmicParser()::parse, ui.text("axis.power"));
            }
        } else if(signalSource == LIN_PROBE) {
            if(normalized) {
                chartContext.valueProcessor = new LinProbeNormProcessor(radio3.getLinearParser()::parse, chartContext.receivedData, ui.text("axisRelativeVoltage"));
            } else {
                chartContext.valueProcessor = new LinProbeProcessor(radio3.getLinearParser()::parse, ui.text("axis.voltage"));
            }
        } else {
            throw new IllegalStateException("source probe: "+sourceProbe.getValue());
        }

        signalAxisX.setLabel(chartContext.valueProcessor.axisLabel()+ " / "+ ui.text("axis.freq"));
    }

    private Frequency scenePosToFrequency(Point2D scenePos) {
        double axisX = signalAxisX.sceneToLocal(scenePos).getX();
        return new Frequency(signalAxisX.getValueForDisplay(axisX).doubleValue(), MEGA);
    }

    private Point2D valueToRefPos(double value) {
        return anchorPane.sceneToLocal(signalAxisY.localToScene(0, signalAxisY.getDisplayPosition(value)));
    }

    private void initSignalChart() {
        signalAxisX = new NumberAxis(0, 52, 5);
        signalAxisY = new NumberAxis(-60, 10, 5);
        signalChart = new EnhancedLineChart<>(signalAxisX, signalAxisY);
        signalChart.legendVisibleProperty().setValue(false);
        signalChart.setAnimated(false);
        chartBox.getChildren().add(signalChart);
        HBox.setHgrow(signalChart, Priority.ALWAYS);
    }

    public void initialize() {
        super.initialize();
        
        initSignalChart();

        filterToolController = new FilterToolController(ui, signalChart, chartContext);
        anchorPane.boundsInLocalProperty().addListener((observable, oldValue, newValue) -> anchorPane.requestLayout());

        chartMarker.initialize(anchorPane, signalChart, scenePos -> {
            if(signalDataSeries.isEmpty()) return null;
            
            Frequency freq = scenePosToFrequency(scenePos);
            double value = valueFromSeries(signalDataSeries.get(0), freq.to(MEGA));
            Point2D selectionPos = new Point2D(scenePos.getX(), valueToRefPos(value).getY());
            return new ChartMarker.SelectionData(selectionPos, "f = "+freq+"\n" + chartContext.valueProcessor.valueLabel()+" = "+ chartContext.valueProcessor.format(value));
        }, () -> !btnContinuous.isSelected(), () -> !btnNormalize.isSelected());

        chartMarker.setRangeHandler((startData, endData) -> sweepSettingsController.setFrequencyRange(
                startData.getXValue().doubleValue(),
                endData.getXValue().doubleValue()
        ));

        signalDataSeries = FXCollections.observableArrayList();
        signalChart.setData(signalDataSeries);
        signalChart.setCreateSymbols(false);

        controlBox.getChildren().add(0, ui.loadFXml(sweepSettingsController, "sweepSettingsPane.fxml"));

        btnNormalize.selectedProperty().addListener(this::normalizeChangeListener);

        initInputProbeList();
        initChartContext();
        initChartTools();
        initRangeControls();
        
        updateNormButton();
    }

    private void initRangeControls() {
        btnAutoRange.setOnAction(event -> autoRangeValueAxis());
        btnPanUp.setOnAction(event -> FxChartUtils.panAxis(signalAxisY, -signalAxisY.getTickUnit()));
        btnPanDown.setOnAction(event -> FxChartUtils.panAxis(signalAxisY, signalAxisY.getTickUnit()));
        btnZoomIn.setOnAction(event -> FxChartUtils.scaleAxis(signalAxisY, 0.5));
        btnZoomOut.setOnAction(event -> FxChartUtils.scaleAxis(signalAxisY, 2));
    }

    private void initChartTools() {
        chartToolsAccordion = new Accordion();
        chartToolsAccordion.setMinWidth(180);

        chartToolsAccordion.getPanes().add(rangeToolController.getTitledPane());
        chartToolsAccordion.setExpandedPane(rangeToolController.getTitledPane());
        chartToolsAccordion.getPanes().add(filterToolController.getTitledPane());

        chartTools = new VBox(chartToolsAccordion);

        btnTools.selectedProperty().addListener((observable, oldValue, selected) -> {
            if(selected && !chartBox.getChildren().contains(chartTools)) {
                chartBox.getChildren().add(chartTools);
            } else {
                chartBox.getChildren().remove(chartTools);
            }
        });
        btnTools.setSelected(true);
    }

    protected void sweepSettingsChangeListener() {
        clear();
        updateNormButton();
        onSweepOnce();
    }

    private void normalizeChangeListener(ObservableValue<? extends Boolean> ob, Boolean ov, Boolean normalized) {
        if(!chartContext.isReady()) {
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
        rangeToolController.clear();
        filterToolController.off();
        filterToolController.setDisable(source != LOG_PROBE);
    }

    private void initInputProbeList() {
        sourceProbe.getItems().add(LOG_PROBE);
        sourceProbe.getItems().add(LIN_PROBE);
        sourceProbe.getSelectionModel().selectFirst();
        sourceProbe.getSelectionModel().selectedItemProperty().addListener(this::inputSourceChangeListener);
    }

    protected void disableUI() {
        FxUtils.disableItems(btnOnce, btnNormalize, sourceProbe);
        sweepSettingsController.disableControls(true);
        mainController.disableAllExcept(true, mainController.sweepTab);
        if(!btnContinuous.isSelected()) btnContinuous.setDisable(true);
    }

    protected void enableUI() {
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

    public void updateAnalyserData(SweepResponse ad) {
        receivedDataInfo = ad.toInfo();
        int samples[] = ad.getData()[0];
        int freq = ad.getFreqStart().value;
        int freqStep = ad.getFreqStep().value;
        chartContext.init(samples.length);
        for (int step = 0; step <= ad.getNumSteps(); step++) {
            chartContext.setReceivedData(step, samples[step],  MEGA.fromBase(freq));
            freq += freqStep;
        }

        updateChart();
    }
    
    protected Response<SweepResponse> sweepOnce() {
        SweepQuality quality = sweepSettingsController.getQuality();
        Frequency fStart = sweepSettingsController.getStartFrequency();
        Frequency fEnd = sweepSettingsController.getEndFrequency();
        Frequency fStep = new Frequency((fEnd.value - fStart.value) / quality.getSteps());
        return radio3.startAnalyser(fStart, fStep,
                quality.getSteps(), quality.getAvgPasses(), quality.getAvgSamples(), sourceProbe.getValue());
    }

    private void updateNormButton() {
        btnNormalize.setDisable(chartContext.isReady());
    }

    private void updateChart() {
        chartMarker.clear();
        signalDataSeries.clear();
        rangeToolController.clear();
        filterToolController.clear();

        if(receivedDataInfo==null) { return; }

        XYChart.Series<Number, Number> chartSeries = new XYChart.Series<>();
        chartSeries.setName(receivedDataInfo.getSource().getSeriesTitle(0));
        ObservableList<Data<Number, Number>> data = chartSeries.getData();
        signalDataSeries.add(chartSeries);

        for (int step = 0; step < chartContext.getDataSize(); step++) {
            double processedValue = chartContext.setAndGetProcessedData(step);
            data.add(new Data<>(chartContext.receivedFreq[step], processedValue));
        }
        
        FrequencyAxisUtils.setupFrequencyAxis(signalAxisX, receivedDataInfo.getFreqStart(), receivedDataInfo.getFreqEnd());
        rangeToolController.update();
        filterToolController.update();
    }
    
    private void autoRangeValueAxis() {
        Range range = new Range();
        for(int i=0; i<chartContext.processedData.length; i++) {
            range.sample(chartContext.processedData[i]);
        }

        switch (sourceProbe.getValue()) {
            case LIN_PROBE:
                FxChartUtils.autoRangeAxis(signalAxisY, range, 0.02, 0.01);
                break;
            default:
                FxChartUtils.autoRangeAxis(signalAxisY, range, 6, 1);
        }
    }

    void clear() {
        chartMarker.clear();
        signalDataSeries.clear();
        rangeToolController.clear();
        filterToolController.clear();
        chartContext.clear();
    }
}