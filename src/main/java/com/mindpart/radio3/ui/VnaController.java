package com.mindpart.radio3.ui;

import com.mindpart.radio3.SweepProfile;
import com.mindpart.radio3.Sweeper;
import com.mindpart.radio3.VnaParser;
import com.mindpart.radio3.VnaResult;
import com.mindpart.radio3.device.AnalyserData;
import com.mindpart.radio3.device.AnalyserDataSource;
import com.mindpart.radio3.device.AnalyserState;
import com.mindpart.types.Frequency;
import com.mindpart.types.SWR;
import com.mindpart.ui.ChartMarker;
import com.mindpart.utils.FxUtils;
import com.mindpart.utils.Range;
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
import javafx.scene.control.Label;
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
    private static final double MAX_SWR = 5.0;
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
    Label statusLabel;

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
    private Sweeper sweeper;
    private VnaParser vnaParser;
    private SweepSettingsPane sweepSettingsPane;
    private ChartMarker chartMarker = new ChartMarker();
    private MainController mainController;

    public VnaController(MainController mainController, Sweeper sweeper, VnaParser vnaParser, List<SweepProfile> sweepProfiles) {
        this.mainController = mainController;
        this.sweeper = sweeper;
        this.vnaParser = vnaParser;
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
        statusLabel.setText("ready");
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
            sweepOnce(SweepQuality.FAST, this::displayDataAndSweepAgain);
            btnContinuous.setText("Stop");
        } else {
            FxUtils.enableItems(btnStart);
            sweepSettingsPane.disableControls(false);
            mainController.disableAllExcept(false, mainController.vnaTab);
            btnContinuous.setText("Continuous");
        }
    }

    private void displayDataAndSweepAgain(AnalyserData analyserData) {
        if(btnContinuous.isSelected()) {
            updateAnalyserData(analyserData);
            sweepOnce(SweepQuality.FAST, this::displayDataAndSweepAgain);
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
        sweepOnce(sweepSettingsPane.getQuality(), this::updateAnalyserData);
        statusLabel.setText("started");
    }

    private void sweepOnce(SweepQuality quality, Consumer<AnalyserData> dataHandler) {
        long fStart = sweepSettingsPane.getStartFrequency().toHz();
        long fEnd = sweepSettingsPane.getEndFrequency ().toHz();
        int fStep = (int) ((fEnd - fStart) / quality.getSteps());
        sweeper.startAnalyser(fStart, fStep, quality, AnalyserDataSource.VNA, dataHandler);
    }

    public void updateAnalyserState(AnalyserState state) {
        statusLabel.setText(state.toString());
    }

    public void updateAnalyserData(AnalyserData ad) {
        chartMarker.reset();
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
        swrChart.getData().clear();
        impedanceChart.getData().clear();

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
            VnaResult vnaResult = vnaParser.calculateVnaResult(samples[0][num], samples[1][num]);
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

    private void updateSwrAxis(Range swrRange) {
        if(swrRange.isValid()) {
            if(swrRange.span() < 1.95) {
                setUpAxis(swrAxisY, 0, 2, 0.1);
            } else if(swrRange.span() < 4.5) {
                setUpAxis(swrAxisY, 0, 5, 0.5);
            } else if(swrRange.span() < 490) {
                setUpAxis(swrAxisY, 0, 500, 50);
            } else if(swrRange.span() < 1999) {
                setUpAxis(swrAxisY, 0, 2000, 200);
            } else {
                swrAxisY.setAutoRanging(true);
            }
        } else {
            swrAxisY.setAutoRanging(true);
        }
    }

    private void updateImpedanceAxis(Range swrRange) {
        if(swrRange.isValid()) {
            if(swrRange.span() < 24) {
                setUpAxis(impedanceAxisY, 0, 25, 1);
            } else if(swrRange.span() < 99) {
                setUpAxis(impedanceAxisY, 0, 100, 5);
            } else if(swrRange.span() < 990) {
                setUpAxis(impedanceAxisY, 0, 1000, 50);
            } else if(swrRange.span() < 9900) {
                setUpAxis(impedanceAxisY, 0, 10000, 500);
            } else {
                impedanceAxisY.setAutoRanging(true);
            }
        } else {
            impedanceAxisY.setAutoRanging(true);
        }
    }

}