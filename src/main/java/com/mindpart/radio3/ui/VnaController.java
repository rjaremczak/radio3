package com.mindpart.radio3.ui;

import com.mindpart.radio3.VnaResult;
import com.mindpart.radio3.device.Radio3;
import com.mindpart.radio3.device.Response;
import com.mindpart.radio3.device.SweepResponse;
import com.mindpart.radio3.device.SweepSignalSource;
import com.mindpart.science.Frequency;
import com.mindpart.science.SWR;
import com.mindpart.science.UnitPrefix;
import com.mindpart.ui.ChartMarker;
import com.mindpart.ui.FxChartUtils;
import com.mindpart.ui.FxUtils;
import com.mindpart.numeric.Range;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;

import java.text.DecimalFormat;
import java.text.NumberFormat;

import static com.mindpart.science.UnitPrefix.MEGA;
import static com.mindpart.ui.FxChartUtils.autoRangeAxis;
import static com.mindpart.ui.FxUtils.valueFromSeries;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.15
 */
public class VnaController extends AbstractSweepController {
    private static final NumberFormat RX_FORMAT = new DecimalFormat("0.0");

    @FXML
    AnchorPane anchorPane;

    @FXML
    HBox controlBox;

    @FXML
    HBox chartBox;

    @FXML
    LineChart<Number, Number> swrChart;

    @FXML
    NumberAxis swrAxisX;

    @FXML
    NumberAxis swrAxisY;

    @FXML
    LineChart<Number, Number> impedanceChart;

    @FXML
    NumberAxis impedanceAxisX;

    @FXML
    NumberAxis impedanceAxisY;

    private ObservableList<XYChart.Series<Number, Number>> swrDataSeries;
    private ObservableList<XYChart.Series<Number, Number>> impedanceDataSeries;
    private ChartMarker chartMarker = new ChartMarker();
    private MainController mainController;

    public VnaController(Radio3 radio3, MainController mainController) {
        super(radio3, mainController.ui);
        this.mainController = mainController;
    }

    private Frequency scenePosToFrequency(Point2D scenePos) {
        double axisX = swrAxisX.sceneToLocal(scenePos).getX();
        return new Frequency(swrAxisX.getValueForDisplay(axisX).doubleValue(), MEGA);
    }

    private Point2D swrToLocalPos(SWR swr) {
        return anchorPane.sceneToLocal(swrAxisY.localToScene(0,swrAxisY.getDisplayPosition(swr.getValue())));
    }

    public void initialize() {
        super.initialize();
        
        swrDataSeries = FXCollections.observableArrayList();
        swrChart.setData(swrDataSeries);
        swrChart.setCreateSymbols(false);

        chartMarker.initialize(anchorPane, swrChart, scenePos -> {
            if(swrDataSeries.isEmpty()) return null;
            
            Frequency freq = scenePosToFrequency(scenePos);
            SWR swr = new SWR(valueFromSeries(swrDataSeries.get(0), freq.to(MEGA)));
            double r = valueFromSeries(impedanceDataSeries.get(0), freq.to(MEGA));
            double x = valueFromSeries(impedanceDataSeries.get(1), freq.to(MEGA));
            Point2D selectionPos = new Point2D(scenePos.getX(), swrToLocalPos(swr).getY());
            return new ChartMarker.SelectionData(selectionPos , "f = "+freq+"\nSWR = "+swr+"\nZ = "+RX_FORMAT.format(r)+" + j"+RX_FORMAT.format(x)+" Ω");
        }, () -> !btnContinuous.isSelected(), () -> true);

        chartMarker.setRangeHandler((startData, endData) -> sweepSettingsController.setFrequencyRange(
                startData.getXValue().doubleValue(),
                endData.getXValue().doubleValue()
        ));

        impedanceDataSeries = FXCollections.observableArrayList();
        impedanceChart.setData(impedanceDataSeries);
        impedanceChart.setCreateSymbols(false);

        FxChartUtils.autoRangeAxis(swrAxisX, 1, 55, 2.5);
        FxChartUtils.autoRangeAxis(swrAxisY, 0, 200, 10);

        FxChartUtils.autoRangeAxis(impedanceAxisX, 1, 55, 2.5);
        FxChartUtils.autoRangeAxis(impedanceAxisY, 0, 1000, 50);

        Parent sweepSettingsPane = ui.loadFXml(sweepSettingsController, "sweepSettingsPane.fxml");
        controlBox.getChildren().add(0, sweepSettingsPane);

        swrAxisX.setLabel(ui.text("axis.swr")+" / "+ui.text("axis.freq"));
        swrAxisY.setLabel("");
        impedanceAxisX.setLabel(ui.text("axis.impedance")+" / "+ui.text("axis.freq"));
        impedanceAxisY.setLabel("");

    }

    protected void sweepSettingsChangeListener() {
        clear();
        onSweepOnce();
    }

    protected void disableUI() {
        FxUtils.disableItems(btnOnce);
        sweepSettingsController.disableControls(true);
        mainController.disableAllExcept(true, mainController.vnaTab);
        if(!btnContinuous.isSelected()) btnContinuous.setDisable(true);
    }

    protected void enableUI() {
        if(!btnContinuous.isSelected()) btnContinuous.setDisable(false);
        FxUtils.enableItems(btnOnce);
        sweepSettingsController.disableControls(false);
        mainController.disableAllExcept(false, mainController.vnaTab);
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

    protected Response<SweepResponse> sweepOnce() {
        SweepQuality quality = sweepSettingsController.getQuality();
        Frequency fStart = sweepSettingsController.getStartFrequency();
        Frequency fEnd = sweepSettingsController.getEndFrequency ();
        Frequency fStep = new Frequency(((fEnd.value - fStart.value) / quality.getSteps()));
        return radio3.startAnalyser(fStart, fStep,
                quality.getSteps(), quality.getAvgPasses(), quality.getAvgSamples(),
                SweepSignalSource.VNA);
    }

    public void updateAnalyserData(SweepResponse ad) {
        chartMarker.clear();
        Frequency freqEnd = new Frequency(ad.getFreqStart().value + (ad.getNumSteps() * ad.getFreqStep().value));
        int samples[][] = ad.getData();

        FrequencyAxisUtils.setupFrequencyAxis(swrAxisX, ad.getFreqStart(), freqEnd);
        FrequencyAxisUtils.setupFrequencyAxis(impedanceAxisX, ad.getFreqStart(), freqEnd);

        updateCharts(ad.getFreqStart(), ad.getFreqStep(), ad.getNumSteps(), samples);
    }

    private XYChart.Series<Number,Number> createSeries(String name) {
        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        series.setName(name);
        return series;
    }

    private void updateCharts(Frequency freqStart, Frequency freqStep, int numSteps, int samples[][]) {
        clear();

        XYChart.Series<Number, Number> swrSeries = createSeries("SWR");
        XYChart.Series<Number, Number> rSeries = createSeries("R");
        XYChart.Series<Number, Number> xSeries = createSeries("X");

        ObservableList<XYChart.Data<Number, Number>> swrData = swrSeries.getData();
        ObservableList<XYChart.Data<Number, Number>> rData = rSeries.getData();
        ObservableList<XYChart.Data<Number, Number>> xData = xSeries.getData();

        Range swrRange = new Range();
        Range impedanceRange = new Range();
        long freq = freqStart.value;
        for (int num = 0; num <= numSteps; num++) {
            VnaResult vnaResult = radio3.getProbesParser().convertVnaValue(samples[0][num], samples[1][num]);
            double fMHz = MEGA.fromBase(freq);
            swrData.add(new XYChart.Data<>(fMHz, swrRange.sample(vnaResult.getSwr())));
            rData.add(new XYChart.Data<>(fMHz, impedanceRange.sample(vnaResult.getR())));
            xData.add(new XYChart.Data<>(fMHz, impedanceRange.sample(vnaResult.getX())));
            freq += freqStep.value;
        }

        swrChart.getData().add(swrSeries);
        impedanceChart.getData().addAll(rSeries, xSeries);
        autoRangeAxis(swrAxisY, swrRange, 2, 0, Double.MAX_VALUE, 1);
        autoRangeAxis(impedanceAxisY, impedanceRange, 10, 0, Double.MAX_VALUE, 1);
    }

    void clear() {
        chartMarker.clear();
        swrDataSeries.clear();
        impedanceDataSeries.clear();
    }
}