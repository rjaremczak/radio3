package com.mindpart.radio3.ui;

import com.mindpart.radio3.SweepProfile;
import com.mindpart.radio3.Sweeper;
import com.mindpart.radio3.VnaParser;
import com.mindpart.radio3.VnaResult;
import com.mindpart.radio3.device.AnalyserData;
import com.mindpart.radio3.device.AnalyserDataSource;
import com.mindpart.radio3.device.AnalyserState;
import com.mindpart.types.Frequency;
import com.mindpart.types.Phase;
import com.mindpart.types.SWR;
import com.mindpart.ui.ChartMarker;
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
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.List;

import static com.mindpart.utils.FxUtils.valueFromSeries;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.15
 */
public class VnaController {
    private static final double MAX_SWR = 5.0;

    @FXML
    AnchorPane anchorPane;

    @FXML
    VBox vBox;

    @FXML
    HBox hBox;

    @FXML
    Button startButton;

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
    private ObservableList<XYChart.Series<Number, Number>> phaseDataSeries;
    private Sweeper sweeper;
    private VnaParser vnaParser;
    private SweepSettings sweepSettings;
    private ChartMarker chartMarker = new ChartMarker();

    public VnaController(Sweeper sweeper, VnaParser vnaParser, List<SweepProfile> sweepProfiles) {
        this.sweeper = sweeper;
        this.vnaParser = vnaParser;
        this.sweepSettings = new SweepSettings(sweepProfiles);
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
            Phase phase = new Phase(valueFromSeries(phaseDataSeries.get(0), freq.toMHz()));
            Point2D selectionPos = new Point2D(scenePos.getX(), swrToLocalPos(swr).getY());
            return new ChartMarker.SelectionData(selectionPos , "freq: "+freq+"\nswr: "+swr+"\nphase: "+phase);
        });

        chartMarker.setupRangeSelection(
                data -> sweepSettings.setStartFrequency(Frequency.ofMHz(data.getXValue().doubleValue())),
                data -> sweepSettings.setEndFrequency(Frequency.ofMHz(data.getXValue().doubleValue())));

        phaseDataSeries = FXCollections.observableArrayList();
        impedanceChart.setData(phaseDataSeries);
        impedanceChart.setCreateSymbols(false);

        setUpAxis(swrAxisX, 1, 55, 2.5);
        //setUpAxis(swrAxisY, -30, +30, 10);
        swrAxisY.setAutoRanging(true);

        setUpAxis(impedanceAxisX, 1, 55, 2.5);
        impedanceAxisY.setAutoRanging(true);
        //setUpAxis(phaseAxisY, 0, 180, 30);

        hBox.getChildren().add(0, sweepSettings);
    }

    private void setUpAxis(Axis<Number> axis, double min, double max, double tickUnit) {
        NumberAxis numberAxis = (NumberAxis) axis;
        numberAxis.setAutoRanging(false);
        numberAxis.setLowerBound(min);
        numberAxis.setUpperBound(max);
        numberAxis.setTickUnit(tickUnit);
    }

    public void doStart() {
        long fStart = sweepSettings.getStartFrequency().toHz();
        long fEnd = sweepSettings.getEndFrequency ().toHz();
        int steps = sweepSettings.getSteps();
        int fStep = (int) ((fEnd - fStart) / steps);
        sweeper.startAnalyser(fStart, fStep, steps, AnalyserDataSource.VNA, this::updateAnalyserData);
        statusLabel.setText("started");
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

        long freq = freqStart;
        for (int num = 0; num <= numSteps; num++) {
            VnaResult vnaResult = vnaParser.calculateVnaResult(samples[0][num], samples[1][num]);
            double fMHz = Frequency.toMHz(freq);
            swrData.add(new XYChart.Data<>(fMHz, vnaResult.getSwr()));
            rData.add(new XYChart.Data<>(fMHz, vnaResult.getR()));
            xData.add(new XYChart.Data<>(fMHz, vnaResult.getX()));
            freq += freqStep;
        }

        swrChart.getData().add(swrSeries);
        impedanceChart.getData().addAll(rSeries, xSeries);
    }
}