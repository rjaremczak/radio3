package com.mindpart.radio3.ui;

import com.mindpart.radio3.SweepStatistics;
import javafx.scene.chart.XYChart;
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
    private final SweepStatistics sweepStatistics = new SweepStatistics();
    private VBox infoBox;
    private Label minValueLabel = new Label();
    private Label minFreqLabel = new Label();
    private Label maxValueLabel = new Label();
    private Label maxFreqLabel = new Label();

    public SweepInfoController(VBox infoBox) {
        this.infoBox = infoBox;
    }

    public void initialize() {
        GridPane gridPane = new GridPane();
        gridPane.add(new Label("min."), 0, 0);
        gridPane.add(minValueLabel, 1, 0);
        gridPane.add(minFreqLabel, 1, 1);

        gridPane.add(new Label("max."), 0, 2);
        gridPane.add(maxValueLabel, 1, 2);
        gridPane.add(maxFreqLabel, 1, 3);
        
        TitledPane statsPane = new TitledPane("General", gridPane);
        infoBox.getChildren().add(statsPane);
    }

    public void update(ChartContext<Integer, Double> chartContext, List<XYChart.Data<Number, Number>> data) {
        if(data!=null && !data.isEmpty()) {
            sweepStatistics.update(data);
            minValueLabel.setText(chartContext.format(sweepStatistics.getMinValue()));
            minFreqLabel.setText("at freq:"+sweepStatistics.getMinFrequency().format());
            maxValueLabel.setText(chartContext.format(sweepStatistics.getMaxValue()));
            maxFreqLabel.setText("at freq:"+sweepStatistics.getMaxFrequency().format());
        } else {
            minValueLabel.setText("-");
            minFreqLabel.setText("-");
            maxValueLabel.setText("-");
            maxFreqLabel.setText("-");
        }
    }
}
