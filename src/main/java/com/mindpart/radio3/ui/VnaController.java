package com.mindpart.radio3.ui;

import com.mindpart.radio3.SweepProfile;
import com.mindpart.radio3.Sweeper;
import com.mindpart.radio3.VnaProbe;
import com.mindpart.radio3.device.AnalyserData;
import com.mindpart.radio3.device.AnalyserDataSource;
import com.mindpart.radio3.device.AnalyserState;
import com.mindpart.types.Frequency;
import com.mindpart.types.Phase;
import com.mindpart.types.SWR;
import com.mindpart.ui.ChartMarker;
import com.mindpart.utils.Range;
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
import java.util.function.IntToDoubleFunction;

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
    LineChart<Number, Number> phaseChart;

    @FXML
    NumberAxis phaseAxisY;

    @FXML
    NumberAxis phaseAxisX;

    private ObservableList<XYChart.Series<Number, Number>> swrDataSeries;
    private ObservableList<XYChart.Series<Number, Number>> phaseDataSeries;
    private Sweeper sweeper;
    private VnaProbe vnaProbe;
    private SweepSettings sweepSettings;
    private ChartMarker chartMarker = new ChartMarker();

    public VnaController(Sweeper sweeper, VnaProbe vnaProbe, List<SweepProfile> sweepProfiles) {
        this.sweeper = sweeper;
        this.vnaProbe = vnaProbe;
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
        phaseChart.setData(phaseDataSeries);
        phaseChart.setCreateSymbols(false);

        setUpAxis(swrAxisX, 1, 55, 2.5);
        setUpAxis(swrAxisY, 1, 5, 0.25);
        setUpAxis(phaseAxisX, 1, 55, 2.5);
        setUpAxis(phaseAxisY, 0, 180, 45);

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
        sweeper.startAnalyser(fStart, fStep, steps, AnalyserDataSource.VNA, this::updateAnalyserData, this::updateAnalyserState);
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
        FrequencyAxisUtils.setupFrequencyAxis(phaseAxisX, ad.getFreqStart(), freqEnd);

        swrAxisY.setAutoRanging(false);
        Range swrRange = updateChart(swrChart, ad.getFreqStart(), ad.getFreqStep(), ad.getNumSteps(), samples[0],
                adcValue -> Math.min(MAX_SWR, vnaProbe.calculateSWR(adcValue)));

        swrAxisY.setLowerBound(Math.min(1.0, swrRange.getMin()));
        swrAxisY.setUpperBound(Math.max(2.0, swrRange.getMax()));
        swrAxisY.setTickUnit(swrRange.span() < 5 ? 0.2 : (swrRange.span() < 20 ? 1.0 : 10.0));

        updateChart(phaseChart, ad.getFreqStart(), ad.getFreqStep(), ad.getNumSteps(), samples[1], vnaProbe::calculatePhaseAngle);
    }

    private Range updateChart(LineChart<Number, Number> chart, long freqStart, long freqStep, int numSteps, int samples[], IntToDoubleFunction translate) {
        chart.getData().clear();
        XYChart.Series<Number, Number> chartSeries = new XYChart.Series<>();
        ObservableList<XYChart.Data<Number, Number>> data = chartSeries.getData();
        long freq = freqStart;
        double minValue = Double.MAX_VALUE;
        double maxValue = Double.MIN_VALUE;
        for (int num = 0; num <= numSteps; num++) {
            double value = translate.applyAsDouble(samples[num]);
            minValue = Math.min(minValue, value);
            maxValue = Math.max(maxValue, value);
            XYChart.Data item = new XYChart.Data(Frequency.toMHz(freq), value);
            data.add(item);
            freq += freqStep;
        }
        chart.getData().add(chartSeries);
        return new Range(minValue, maxValue);
    }
}