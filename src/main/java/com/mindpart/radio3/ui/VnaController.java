package com.mindpart.radio3.ui;

import com.mindpart.radio3.VnaResult;
import com.mindpart.radio3.device.Radio3;
import com.mindpart.radio3.device.Response;
import com.mindpart.radio3.device.SweepResponse;
import com.mindpart.radio3.device.SweepSignalSource;
import com.mindpart.types.Frequency;
import com.mindpart.types.SWR;
import com.mindpart.ui.ChartMarker;
import com.mindpart.ui.FxUtils;
import com.mindpart.numeric.Range;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.function.Consumer;

import static com.mindpart.ui.FxChartUtils.rangeAxis;
import static com.mindpart.ui.FxUtils.valueFromSeries;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.15
 */
public class VnaController {
    private static final NumberFormat RX_FORMAT = new DecimalFormat("0.0");

    @FXML
    AnchorPane anchorPane;

    @FXML
    VBox vBox;

    @FXML
    HBox controlBox;

    @FXML
    HBox chartBox;

    @FXML
    Button btnStart;

    @FXML
    ToggleButton btnContinuous;

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

    private Radio3 radio3;
    private ObservableList<XYChart.Series<Number, Number>> swrDataSeries;
    private ObservableList<XYChart.Series<Number, Number>> impedanceDataSeries;
    private SweepSettingsController sweepSettingsController;
    private ChartMarker chartMarker = new ChartMarker();
    private MainController mainController;

    public VnaController(Radio3 radio3, MainController mainController) {
        this.radio3 = radio3;
        this.mainController = mainController;
        this.sweepSettingsController = new SweepSettingsController(mainController.bundle, radio3.getSweepProfiles());
    }

    private Frequency scenePosToFrequency(Point2D scenePos) {
        double axisX = swrAxisX.sceneToLocal(scenePos).getX();
        return Frequency.ofMHz(swrAxisX.getValueForDisplay(axisX).doubleValue());
    }

    private Point2D swrToLocalPos(SWR swr) {
        return anchorPane.sceneToLocal(swrAxisY.localToScene(0,swrAxisY.getDisplayPosition(swr.getValue())));
    }

    public void initialize() {
        swrDataSeries = FXCollections.observableArrayList();
        swrChart.setData(swrDataSeries);
        swrChart.setCreateSymbols(false);

        chartMarker.initialize(anchorPane, swrChart, scenePos -> {
            if(swrDataSeries.isEmpty()) return null;
            
            Frequency freq = scenePosToFrequency(scenePos);
            SWR swr = new SWR(valueFromSeries(swrDataSeries.get(0), freq.toMHz()));
            double r = valueFromSeries(impedanceDataSeries.get(0), freq.toMHz());
            double x = valueFromSeries(impedanceDataSeries.get(1), freq.toMHz());
            Point2D selectionPos = new Point2D(scenePos.getX(), swrToLocalPos(swr).getY());
            return new ChartMarker.SelectionData(selectionPos , "f = "+freq+"\nSWR = "+swr+"\nZ = "+RX_FORMAT.format(r)+" + j"+RX_FORMAT.format(x)+" Ω");
        }, () -> !btnContinuous.isSelected());

        chartMarker.setupRangeSelection(
                data -> sweepSettingsController.setStartFrequency(Frequency.ofMHz(data.getXValue().doubleValue())),
                data -> sweepSettingsController.setEndFrequency(Frequency.ofMHz(data.getXValue().doubleValue())));

        impedanceDataSeries = FXCollections.observableArrayList();
        impedanceChart.setData(impedanceDataSeries);
        impedanceChart.setCreateSymbols(false);

        rangeAxis(swrAxisX, 1, 55, 2.5);
        rangeAxis(swrAxisY, 0, 200, 10);

        rangeAxis(impedanceAxisX, 1, 55, 2.5);
        rangeAxis(impedanceAxisY, 0, 1000, 50);

        Parent sweepSettingsPane = mainController.loadFXml(sweepSettingsController, "sweepSettingsPane.fxml");
        controlBox.getChildren().add(0, sweepSettingsPane);

        btnContinuous.selectedProperty().addListener(this::onContinuousChanged);
    }

    private void disableUI() {
        FxUtils.disableItems(btnStart);
        sweepSettingsController.disableControls(true);
        mainController.disableAllExcept(true, mainController.vnaTab);
        if(!btnContinuous.isSelected()) btnContinuous.setDisable(true);
    }

    private void enableUI() {
        if(!btnContinuous.isSelected()) btnContinuous.setDisable(false);
        FxUtils.enableItems(btnStart);
        sweepSettingsController.disableControls(false);
        mainController.disableAllExcept(false, mainController.vnaTab);
        mainController.requestDeviceState();
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

    private void onContinuousChanged(ObservableValue<? extends Boolean> ob, Boolean ov, Boolean continuous) {
        if(continuous) {
            disableUI();
            runSweepOnce(this::displayDataAndSweepAgain);
            btnContinuous.setText("Stop");
        } else {
            enableUI();
            btnContinuous.setText("Continuous");
        }
    }

    private void displayDataAndSweepAgain(SweepResponse analyserResponse) {
        if(btnContinuous.isSelected()) {
            updateAnalyserData(analyserResponse);
            runSweepOnce(this::displayDataAndSweepAgain);
        }
    }

    public void onSweepOnce() {
        disableUI();
        runSweepOnce(analyserResponse -> {
            updateAnalyserData(analyserResponse);
            enableUI();
        });
    }

    private Response<SweepResponse> sweepOnce() {
        SweepQuality quality = sweepSettingsController.getQuality();
        long fStart = sweepSettingsController.getStartFrequency().toHz();
        long fEnd = sweepSettingsController.getEndFrequency ().toHz();
        int fStep = (int) ((fEnd - fStart) / quality.getSteps());
        return radio3.startAnalyser(fStart, fStep,
                quality.getSteps(), quality.getAvgPasses(), quality.getAvgSamples(),
                SweepSignalSource.VNA);
    }

    public void updateAnalyserData(SweepResponse ad) {
        chartMarker.clear();
        long freqEnd = ad.getFreqStart() + (ad.getNumSteps() * ad.getFreqStep());
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

    private void updateCharts(long freqStart, long freqStep, int numSteps, int samples[][]) {
        clear();

        XYChart.Series<Number, Number> swrSeries = createSeries("SWR");
        XYChart.Series<Number, Number> rSeries = createSeries("R - resistance");
        XYChart.Series<Number, Number> xSeries = createSeries("X - reactance");

        ObservableList<XYChart.Data<Number, Number>> swrData = swrSeries.getData();
        ObservableList<XYChart.Data<Number, Number>> rData = rSeries.getData();
        ObservableList<XYChart.Data<Number, Number>> xData = xSeries.getData();

        Range swrRange = new Range();
        Range impedanceRange = new Range();
        long freq = freqStart;
        for (int num = 0; num <= numSteps; num++) {
            VnaResult vnaResult = radio3.getVnaParser().calculateVnaResult(samples[0][num], samples[1][num]);
            double fMHz = Frequency.toMHz(freq);
            swrData.add(new XYChart.Data<>(fMHz, swrRange.sample(vnaResult.getSwr())));
            rData.add(new XYChart.Data<>(fMHz, impedanceRange.sample(vnaResult.getR())));
            xData.add(new XYChart.Data<>(fMHz, impedanceRange.sample(vnaResult.getX())));
            freq += freqStep;
        }

        swrChart.getData().add(swrSeries);
        impedanceChart.getData().addAll(rSeries, xSeries);
        rangeAxis(swrAxisY, swrRange, 2, 0, Double.MAX_VALUE, 1);
        rangeAxis(impedanceAxisY, impedanceRange, 10, 0, Double.MAX_VALUE, 1);
    }

    void clear() {
        chartMarker.clear();
        swrDataSeries.clear();
        impedanceDataSeries.clear();
    }
}