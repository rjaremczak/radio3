package com.mindpart.radio3.ui;

import com.mindpart.radio3.VnaStatistics;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.05
 */
public class VnaInfoController {

    private Parent container;

    @FXML
    Label minSwr;

    @FXML
    Label maxSwr;

    @FXML
    Label avgSwr;

    private final VnaStatistics vnaStatistics = new VnaStatistics();

    public VnaInfoController(MainController mainController) {
        this.container = mainController.loadFXml(this, "vnaInfoPane.fxml");
    }

    public void initialize() {
    }

    public void update(ValueProcessor<Integer, Double> chartValueContext, List<XYChart.Data<Number, Number>> data) {
        if(data!=null && !data.isEmpty()) {
            vnaStatistics.update(data);
            minSwr.setText(chartValueContext.format(vnaStatistics.getMinSwr()));
            maxSwr.setText(chartValueContext.format(vnaStatistics.getMaxSwr()));
            avgSwr.setText(chartValueContext.format(vnaStatistics.getAvgSwr()));
        } else {
            minSwr.setText("");
            maxSwr.setText("");
            avgSwr.setText("");
        }
    }

    public Parent getContainer() {
        return container;
    }
}
