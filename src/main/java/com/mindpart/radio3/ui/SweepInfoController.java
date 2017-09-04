package com.mindpart.radio3.ui;

import com.mindpart.discrete.MaxCheck;
import com.mindpart.discrete.MinCheck;
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
    private MinCheck minCheck = new MinCheck();
    private MaxCheck maxCheck = new MaxCheck();

    public void clear() {
        minCheck.reset();
        maxCheck.reset();

        minValue.setText(NA);
        minFreq.setText(NA);
        maxValue.setText(NA);
        maxFreq.setText(NA);
        spanValue.setText(NA);

        qFreq.setText(NA);
        qBandwidth.setText(NA);
        qValue.setText(NA);
    }

    public void sample(int number, double value) {
        minCheck.sample(number, value);
        maxCheck.sample(number, value);
    }

    public SweepInfoController(MainController mainController) {
        this.container = mainController.loadFXml(this, "sweepInfoPane.fxml");
    }

    public void update(Function<Integer,String> freqSampleFormatter, ChartContext<Integer, Double> chartValueContext) {
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
