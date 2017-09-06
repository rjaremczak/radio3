package com.mindpart.radio3.ui;

import com.mindpart.numeric.MaxCheck;
import com.mindpart.numeric.MinCheck;
import com.mindpart.numeric.QAnalyser;
import com.mindpart.types.Frequency;
import com.mindpart.ui.VerticalRuler;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.function.Function;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.05
 */
public class SweepInfoController {
    private static final NumberFormat FORMAT_Q = new DecimalFormat("0.0");
    private static final String NA = "-";

    @FXML
    TitledPane generalInfoPane;

    @FXML
    Label minValue;

    @FXML
    Label minFreq;

    @FXML
    Label maxValue;

    @FXML
    Label maxFreq;

    @FXML
    Label spanValue;

    @FXML
    TitledPane qPane;

    @FXML
    Label qFreq;

    @FXML
    Label qBandwidth;

    @FXML
    Label qValue;

    private final Parent container;
    private final VerticalRuler qFreqRuler;
    private final VerticalRuler qBandwidthStartRuler;
    private final VerticalRuler qBandwidthEndRuler;

    public void clear() {
        minValue.setText(NA);
        minFreq.setText(NA);
        maxValue.setText(NA);
        maxFreq.setText(NA);
        spanValue.setText(NA);

        clearQPane();
    }

    private void clearQPane() {
        qFreq.setText(NA);
        qBandwidth.setText(NA);
        qValue.setText(NA);

        qFreqRuler.hide();
        qBandwidthStartRuler.hide();
        qBandwidthEndRuler.hide();
    }

    public SweepInfoController(MainController mainController, Pane chartParent, XYChart<Number, Number> chart) {
        this.container = mainController.loadFXml(this, "sweepInfoPane.fxml");
        this.qFreqRuler = new VerticalRuler(chartParent, chart, Color.DARKBLUE);
        this.qBandwidthStartRuler = new VerticalRuler(chartParent, chart, Color.web("#0000FF", 0.2));
        this.qBandwidthEndRuler = new VerticalRuler(chartParent, chart, Color.web("#0000FF", 0.2));

        qPane.expandedProperty().addListener(this::qPaneVisibilityListener);
    }

    private void qPaneVisibilityListener(ObservableValue<? extends Boolean> ob, Boolean ov, Boolean expanded) {
        clearQPane();
    }

    public void update(double[] freq, double[] data, Function<Integer,String> freqSampleFormatter, ChartContext<Integer, Double> chartValueContext) {
        MinCheck minCheck = new MinCheck();
        MaxCheck maxCheck = new MaxCheck();

        for(int i=0; i<data.length; i++) {
            minCheck.sample(i, data[i]);
            maxCheck.sample(i, data[i]);
        }

        minValue.setText(chartValueContext.format(minCheck.getSampleValue()));
        minFreq.setText(freqSampleFormatter.apply(minCheck.getSampleNumber()));
        maxValue.setText(chartValueContext.format(maxCheck.getSampleValue()));
        maxFreq.setText(freqSampleFormatter.apply(maxCheck.getSampleNumber()));
        spanValue.setText(chartValueContext.format(maxCheck.getSampleValue() - minCheck.getSampleValue()));

        if(qPane.isExpanded() && chartValueContext instanceof LogarithmicProbeContext) {
            QAnalyser qAnalyser = new QAnalyser(data, freq);
            if (minCheck.isFound() && qAnalyser.analyseLowPeak(3.0)) {
                qFreq.setText(Frequency.ofMHz(qAnalyser.getPeakFreq()).format());
                qBandwidth.setText(Frequency.ofMHz(qAnalyser.getBandwidth()).format());
                qValue.setText(FORMAT_Q.format(qAnalyser.getQ()));

                qBandwidthStartRuler.update(qAnalyser.getStartFreq());
                qBandwidthEndRuler.update(qAnalyser.getEndFreq());
                qFreqRuler.update(qAnalyser.getPeakFreq());

                qBandwidthStartRuler.show();
                qBandwidthEndRuler.show();
                qFreqRuler.show();
            } else {
                clearQPane();
            }
        }
    }

    public Parent getContainer() {
        return container;
    }
}
