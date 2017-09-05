package com.mindpart.radio3.ui;

import com.mindpart.discrete.MaxCheck;
import com.mindpart.discrete.MinCheck;
import com.mindpart.discrete.QAnalyser;
import com.mindpart.types.Frequency;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;

import java.util.function.Function;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.05
 */
public class SweepInfoController {
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

    private Parent container;

    public void clear() {
        minValue.setText(NA);
        minFreq.setText(NA);
        maxValue.setText(NA);
        maxFreq.setText(NA);
        spanValue.setText(NA);

        clearQData();
    }

    private void clearQData() {
        qFreq.setText(NA);
        qBandwidth.setText(NA);
        qValue.setText(NA);
    }

    public SweepInfoController(MainController mainController) {
        this.container = mainController.loadFXml(this, "sweepInfoPane.fxml");
    }

    public void update(double[] data, double[] freq, Function<Integer,String> freqSampleFormatter, ChartContext<Integer, Double> chartValueContext) {
        MinCheck minCheck = new MinCheck();
        MaxCheck maxCheck = new MaxCheck();

        for(int i=0; i<data.length; i++) {
            minCheck.sample(i, data[i]);
            maxCheck.sample(i, data[i]);
        }

        clearQData();
        if(chartValueContext instanceof LogarithmicProbeContext) {
            QAnalyser qAnalyser = new QAnalyser(data, freq);
            if(minCheck.isFound() && qAnalyser.analyseLowPeak(minCheck.getSampleNumber(), 3.0) ) {
                qFreq.setText(Frequency.ofMHz(qAnalyser.getPeakFreq()).format());
                qBandwidth.setText(Frequency.ofMHz(qAnalyser.getBandwidth()).format());
                qValue.setText(Double.toString(qAnalyser.getQ()));
            }
        }

        minValue.setText(chartValueContext.format(minCheck.getSampleValue()));
        minFreq.setText(freqSampleFormatter.apply(minCheck.getSampleNumber()));
        maxValue.setText(chartValueContext.format(maxCheck.getSampleValue()));
        maxFreq.setText(freqSampleFormatter.apply(maxCheck.getSampleNumber()));
        spanValue.setText(chartValueContext.format(maxCheck.getSampleValue() - minCheck.getSampleValue()));
    }

    public Parent getContainer() {
        return container;
    }
}
