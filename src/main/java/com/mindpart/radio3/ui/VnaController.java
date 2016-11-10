package com.mindpart.radio3.ui;

import com.mindpart.radio3.SweepProfile;
import com.mindpart.radio3.Sweeper;
import com.mindpart.radio3.VnaProbe;
import com.mindpart.radio3.device.AnalyserData;
import com.mindpart.radio3.device.AnalyserState;
import com.mindpart.types.Frequency;
import com.mindpart.types.Phase;
import com.mindpart.types.SWR;
import com.mindpart.ui.ChartGuides;
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
import org.apache.log4j.Logger;

import java.util.List;
import java.util.function.IntToDoubleFunction;

/**
 * Created by Robert Jaremczak
 * Date: 2016.04.15
 */
public class VnaController {
    private static final long MHZ = 1000000;
    private static Logger logger = Logger.getLogger(VnaController.class);

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
    LineChart<Number, Number> phaseChart;

    private ObservableList<XYChart.Series<Number, Number>> swrDataSeries;
    private ObservableList<XYChart.Series<Number, Number>> phaseDataSeries;
    private Sweeper sweeper;
    private VnaProbe vnaProbe;
    private SweepConfigControl sweepConfigControl;
    private ChartGuides chartGuides;


    public VnaController(Sweeper sweeper, VnaProbe vnaProbe, List<SweepProfile> sweepProfiles) {
        this.sweeper = sweeper;
        this.vnaProbe = vnaProbe;
        this.sweepConfigControl = new SweepConfigControl(sweepProfiles);
    }

    private Frequency scenePosToFrequency(Point2D scenePos) {
        double axisX = swrChart.getXAxis().sceneToLocal(scenePos).getX();
        return Frequency.ofMHz(swrChart.getXAxis().getValueForDisplay(axisX).doubleValue());
    }

    private double valueFromSeries(XYChart.Series<Number, Number> series, double argument) {
        Number number = null;
        for(XYChart.Data<Number, Number> item : series.getData()) {
            if(item.getXValue().doubleValue() > argument) { break; }
            number = item.getYValue();
        }
        return number!=null ? number.doubleValue() : 0;
    }

    private Point2D swrToLocalPos(SWR swr) {
        return anchorPane.sceneToLocal(swrChart.getYAxis().localToScene(0,swrChart.getYAxis().getDisplayPosition(swr.getValue())));
    }

    public void initialize() {
        statusLabel.setText("ready");
        swrDataSeries = FXCollections.observableArrayList();
        swrChart.setData(swrDataSeries);
        swrChart.setCreateSymbols(false);

        chartGuides = new ChartGuides(anchorPane, swrChart, mousePos -> {
            Frequency freq = scenePosToFrequency(mousePos);
            SWR swr = new SWR(valueFromSeries(swrDataSeries.get(0), freq.toMHz()));
            Phase phase = new Phase(valueFromSeries(phaseDataSeries.get(0), freq.toMHz()));
            Point2D selectionPos = new Point2D(mousePos.getX(), swrToLocalPos(swr).getY());
            return new ChartGuides.SelectionData(selectionPos , "freq: "+freq+"\nswr: "+swr+"\nphase: "+phase);
        });

        swrChart.boundsInLocalProperty().addListener((observable, oldValue, newValue) -> chartGuides.updateChartBounds());
        swrChart.setOnMouseClicked(chartGuides::onMouseClicked);
        swrChart.setOnMouseExited(chartGuides::onMouseExited);

        phaseDataSeries = FXCollections.observableArrayList();
        phaseChart.setData(phaseDataSeries);
        phaseChart.setCreateSymbols(false);

        setUpAxis(swrChart.getXAxis(), 1, 55, 2.5);
        setUpAxis(swrChart.getYAxis(), 1, 5, 0.25);
        setUpAxis(phaseChart.getXAxis(), 1, 55, 2.5);
        setUpAxis(phaseChart.getYAxis(), 0, 180, 45);

        hBox.getChildren().add(0, sweepConfigControl);
    }

    private void setUpAxis(Axis<Number> axis, double min, double max, double tickUnit) {
        NumberAxis numberAxis = (NumberAxis) axis;
        numberAxis.setAutoRanging(false);
        numberAxis.setLowerBound(min);
        numberAxis.setUpperBound(max);
        numberAxis.setTickUnit(tickUnit);
    }

    public void doStart() {
        long fStart = sweepConfigControl.getStartFrequency().toHz();
        long fEnd = sweepConfigControl.getEndFrequency ().toHz();
        int steps = sweepConfigControl.getSteps();
        int fStep = (int) ((fEnd - fStart) / steps);
        sweeper.startAnalyser(fStart, fStep, steps, AnalyserData.Source.VNA, this::updateData, this::updateState);
        statusLabel.setText("started");
    }

    public void updateState(AnalyserState state) {
        statusLabel.setText(state.toString());
    }

    private double autoTickUnit(double valueSpan) {
        for (double div = 0.000001; div <= 100; div *= 10) {
            if (valueSpan < div) {
                return div / 25;
            }
        }
        return 1.0;
    }

    public void updateData(AnalyserData ad) {
        long freqEnd = ad.getFreqStart() + (ad.getNumSteps() * ad.getFreqStep());
        int samples[][] = ad.getData();

        updateFrequencyAxis((NumberAxis) swrChart.getXAxis(), ad.getFreqStart(), freqEnd);
        updateFrequencyAxis((NumberAxis) phaseChart.getXAxis(), ad.getFreqStart(), freqEnd);

        NumberAxis swrAxis = (NumberAxis) swrChart.getYAxis();
        swrAxis.setAutoRanging(false);
        Range swrRange = updateChart(swrChart, ad.getFreqStart(), ad.getFreqStep(), ad.getNumSteps(), samples[0], vnaProbe::calculateSWR);
        swrAxis.setLowerBound(Math.min(1.0, swrRange.getMin()));
        swrAxis.setUpperBound(Math.max(2.0, swrRange.getMax()));
        swrAxis.setTickUnit(swrRange.span() < 5 ? 0.2 : (swrRange.span() < 20 ? 1.0 : 10.0));

        updateChart(phaseChart, ad.getFreqStart(), ad.getFreqStep(), ad.getNumSteps(), samples[1], vnaProbe::calculatePhaseAngle);
    }

    private void updateFrequencyAxis(NumberAxis axis, long freqStart, long freqEnd) {
        Frequency fStart = Frequency.ofHz(freqStart);
        Frequency fEnd = Frequency.ofHz(freqEnd);
        axis.setAutoRanging(false);
        axis.setLowerBound(fStart.toMHz());
        axis.setUpperBound(fEnd.toMHz());
        axis.setTickUnit(autoTickUnit(fEnd.toMHz() - fStart.toMHz()));
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
            XYChart.Data item = new XYChart.Data(((double) freq) / MHZ, value);
            data.add(item);
            freq += freqStep;
        }
        chart.getData().add(chartSeries);
        return new Range(minValue, maxValue);
    }
}