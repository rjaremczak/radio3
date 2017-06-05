package com.mindpart.radio3.ui;

import com.mindpart.radio3.SweepStatistics;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;

import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.05
 */
public class SweepInfoController {
    private static final String MIN_HEADER = "minimum";
    private static final String MAX_HEADER = "maximum";

    private final SweepStatistics sweepStatistics = new SweepStatistics();
    private VBox infoBox;
    private Label minHeader = new Label();
    private Label minValueLabel = new Label();
    private Label minFreqLabel = new Label();
    private Label maxHeader = new Label();
    private Label maxValueLabel = new Label();
    private Label maxFreqLabel = new Label();

    public SweepInfoController(VBox infoBox) {
        this.infoBox = infoBox;
    }

    private ColumnConstraints columnConstraints() {
        ColumnConstraints columnConstraints = new ColumnConstraints();
        columnConstraints.setPercentWidth(50);
        return columnConstraints;
    }

    public void initialize() {
        GridPane gridPane = new GridPane();
        gridPane.add(minHeader, 0, 0, 2, 1);
        gridPane.add(minFreqLabel, 0, 1); gridPane.add(minValueLabel, 1, 1);

        gridPane.add(maxHeader, 0, 2, 2, 1);
        gridPane.add(maxFreqLabel, 0, 3); gridPane.add(maxValueLabel, 1, 3);

        gridPane.getColumnConstraints().addAll(columnConstraints(), columnConstraints());

        TitledPane statsPane = new TitledPane("General", gridPane);
        infoBox.getChildren().add(statsPane);
    }

    public void update(ChartContext<Integer, Double> chartContext, List<XYChart.Data<Number, Number>> data) {
        minHeader.setText(MIN_HEADER + " " + chartContext.valueLabel());
        maxHeader.setText(MAX_HEADER + " " + chartContext.valueLabel());
        
        if(data!=null && !data.isEmpty()) {
            sweepStatistics.update(data);
            infoBox.setDisable(false);
            minFreqLabel.setText(sweepStatistics.getMinFrequency().format());
            minValueLabel.setText(chartContext.format(sweepStatistics.getMinValue()));
            maxFreqLabel.setText(sweepStatistics.getMaxFrequency().format());
            maxValueLabel.setText(chartContext.format(sweepStatistics.getMaxValue()));
        } else {
            infoBox.setDisable(true);
            minValueLabel.setText("-");
            minFreqLabel.setText("-");
            maxValueLabel.setText("-");
            maxFreqLabel.setText("-");
        }
    }
}
