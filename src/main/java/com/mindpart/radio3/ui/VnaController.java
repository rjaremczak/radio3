package com.mindpart.radio3.ui;

import com.mindpart.radio3.SweepProfile;
import com.mindpart.radio3.VnaResult;
import com.mindpart.radio3.device.AnalyserResponse;
import com.mindpart.radio3.device.AnalyserDataSource;
import com.mindpart.radio3.device.Response;
import com.mindpart.types.Frequency;
import com.mindpart.types.SWR;
import com.mindpart.ui.ChartMarker;
import com.mindpart.utils.FxUtils;
import com.mindpart.utils.Range;
import javafx.application.Platform;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.geometry.Point2D;
import javafx.scene.chart.Axis;
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
import java.util.List;
import java.util.function.Consumer;

import static com.mindpart.utils.FxUtils.valueFromSeries;

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
    HBox hBox;

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
    private SweepSettingsPane sweepSettingsPane;
    private ChartMarker chartMarker = new ChartMarker();
    private MainController mainController;

    public VnaController(Radio3 radio3, MainController mainController, List<SweepProfile> sweepProfiles) {
        this.radio3 = radio3;
        this.mainController = mainController;
        this.sweepSettingsPane = new SweepSettingsPane(sweepProfiles);
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
            Frequency freq = scenePosToFrequency(scenePos);
            SWR swr = new SWR(valueFromSeries(swrDataSeries.get(0), freq.toMHz()));
            double r = valueFromSeries(impedanceDataSeries.get(0), freq.toMHz());
            double x = valueFromSeries(impedanceDataSeries.get(1), freq.toMHz());
            Point2D selectionPos = new Point2D(scenePos.getX(), swrToLocalPos(swr).getY());
            return new ChartMarker.SelectionData(selectionPos , "f = "+freq+"\nSWR = "+swr+"\nZ = "+RX_FORMAT.format(r)+" + j"+RX_FORMAT.format(x)+" Ω");
        }, () -> !btnContinuous.isSelected());

        chartMarker.setupRangeSelection(
                data -> sweepSettingsPane.setStartFrequency(Frequency.ofMHz(data.getXValue().doubleValue())),
                data -> sweepSettingsPane.setEndFrequency(Frequency.ofMHz(data.getXValue().doubleValue())));

        impedanceDataSeries = FXCollections.observableArrayList();
        impedanceChart.setData(impedanceDataSeries);
        impedanceChart.setCreateSymbols(false);

        setUpAxis(swrAxisX, 1, 55, 2.5);
        setUpAxis(swrAxisY, 0, 200, 10);
        //swrAxisY.setAutoRanging(true);

        setUpAxis(impedanceAxisX, 1, 55, 2.5);
        setUpAxis(impedanceAxisY, 0, 1000, 50);
        //impedanceAxisY.setAutoRanging(true);

        hBox.getChildren().add(0, sweepSettingsPane);

        btnContinuous.selectedProperty().addListener(this::onContinuousChanged);
    }

    private void onContinuousChanged(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean continuous) {
        if(continuous) {
            FxUtils.disableItems(btnStart);
            sweepSettingsPane.disableControls(true);
            mainController.disableAllExcept(true, mainController.vnaTab);
            runSweepOnce(this::displayDataAndSweepAgain);
            btnContinuous.setText("Stop");
        } else {
            FxUtils.enableItems(btnStart);
            sweepSettingsPane.disableControls(false);
            mainController.disableAllExcept(false, mainController.vnaTab);
            btnContinuous.setText("Continuous");
        }
    }

    private void displayDataAndSweepAgain(AnalyserResponse analyserResponse) {
        if(btnContinuous.isSelected()) {
            updateAnalyserData(analyserResponse);
            runSweepOnce(this::displayDataAndSweepAgain);
        }
    }

    private void setUpAxis(Axis<Number> axis, double min, double max, double tickUnit) {
        NumberAxis numberAxis = (NumberAxis) axis;
        numberAxis.setAutoRanging(false);
        numberAxis.setLowerBound(min);
        numberAxis.setUpperBound(max);
        numberAxis.setTickUnit(tickUnit);
    }

    public void onSweepOnce() {
        runSweepOnce(this::updateAnalyserData);
    }

    private void runSweepOnce(Consumer<AnalyserResponse> analyserDataConsumer) {
        radio3.executeInBackground(() -> {
            Response<AnalyserResponse> response = sweepOnce();
            if(response.isOK()) {
                Platform.runLater(() -> analyserDataConsumer.accept(response.getData()));
            }
        });
    }

    private Response<AnalyserResponse> sweepOnce() {
        SweepQuality quality = sweepSettingsPane.getQuality();
        long fStart = sweepSettingsPane.getStartFrequency().toHz();
        long fEnd = sweepSettingsPane.getEndFrequency ().toHz();
        int fStep = (int) ((fEnd - fStart) / quality.getSteps());
        return radio3.getDeviceService().startAnalyser(fStart, fStep,
                quality.getSteps(), quality.getAvgPasses(), quality.getAvgSamples(),
                AnalyserDataSource.VNA);
    }

    public void updateAnalyserData(AnalyserResponse ad) {
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
            swrData.add(new XYChart.Data<>(fMHz, swrRange.update(vnaResult.getSwr())));
            rData.add(new XYChart.Data<>(fMHz, impedanceRange.update(vnaResult.getR())));
            xData.add(new XYChart.Data<>(fMHz, impedanceRange.update(vnaResult.getX())));
            freq += freqStep;
        }

        swrChart.getData().add(swrSeries);
        impedanceChart.getData().addAll(rSeries, xSeries);
        updateSwrAxis(swrRange);
        updateImpedanceAxis(impedanceRange);
    }

    private void updateSwrAxis(Range range) {
        double min = Math.min(0, range.getMin());
        if(range.isValid()) {
            if(range.getMax() < 1.95) {
                setUpAxis(swrAxisY, min, 2, 0.1);
            } else if(range.getMax() < 4.5) {
                setUpAxis(swrAxisY, min, 5, 0.5);
            } else if(range.getMax() < 19.5) {
                setUpAxis(swrAxisY, min, 20, 2);
            } else if(range.getMax() < 50) {
                setUpAxis(swrAxisY, min, 50, 5);
            } else if(range.getMax() < 90) {
                setUpAxis(swrAxisY, min, 100, 20);
            } else if(range.getMax() < 490) {
                setUpAxis(swrAxisY, min, 500, 50);
            } else if(range.getMax() < 1999) {
                setUpAxis(swrAxisY, min, 2000, 200);
            } else {
                swrAxisY.setAutoRanging(true);
            }
        } else {
            swrAxisY.setAutoRanging(true);
        }
    }

    private void updateImpedanceAxis(Range range) {
        double min = Math.min(0, range.getMin());
        if(range.isValid()) {
            if(range.getMax() < 24) {
                setUpAxis(impedanceAxisY, min, 25, 5);
            } else if(range.getMax() < 99) {
                setUpAxis(impedanceAxisY, min, 100, 10);
            } else if(range.getMax() < 990) {
                setUpAxis(impedanceAxisY, min, 1000, 100);
            } else if(range.getMax() < 1990) {
                setUpAxis(impedanceAxisY, min, 2000, 200);
            } else if(range.getMax() < 4990) {
                setUpAxis(impedanceAxisY, min, 5000, 500);
            } else if(range.getMax() < 9900) {
                setUpAxis(impedanceAxisY, min, 10000, 1000);
            } else {
                impedanceAxisY.setAutoRanging(true);
            }
        } else {
            impedanceAxisY.setAutoRanging(true);
        }
    }

    void clear() {
        chartMarker.clear();
        swrDataSeries.clear();
        impedanceDataSeries.clear();
    }
}