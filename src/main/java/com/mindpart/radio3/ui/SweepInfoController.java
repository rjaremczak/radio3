package com.mindpart.radio3.ui;

import com.mindpart.radio3.SweepStatistics;
import com.mindpart.utils.FxUtils;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Accordion;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.05
 */
public class SweepInfoController {

    private Parent container;

    @FXML
    Label minValue;

    @FXML
    Label maxValue;

    @FXML
    Label spanValue;

    private final SweepStatistics sweepStatistics = new SweepStatistics();

    public SweepInfoController() {
        this.container = FxUtils.loadFXml(this, "sweepInfoPane.fxml");
    }

    public void initialize() {
    }

    public void update(ChartContext<Integer, Double> chartContext, List<XYChart.Data<Number, Number>> data) {
        if(data!=null && !data.isEmpty()) {
            sweepStatistics.update(data);
            minValue.setText(chartContext.format(sweepStatistics.getMinValue()));
            maxValue.setText(chartContext.format(sweepStatistics.getMaxValue()));
            spanValue.setText(chartContext.format(sweepStatistics.getSpanValue()));
        } else {
            minValue.setText("");
            maxValue.setText("");
            spanValue.setText("");
        }
    }

    public Parent getContainer() {
        return container;
    }
}