package com.mindpart.radio3.ui;

import com.mindpart.radio3.VnaStatistics;
import com.mindpart.ui.FxUtils;
import javafx.fxml.FXML;
import javafx.scene.Parent;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;

import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.05
 */
public class VnaInfoController extends BaseController {

    private Parent container;

    @FXML
    Label minSwr;

    @FXML
    Label maxSwr;

    @FXML
    Label avgSwr;

    private final VnaStatistics vnaStatistics = new VnaStatistics();

    public VnaInfoController() {
        this.container = loadFXml("vnaInfoPane.fxml");
    }

    public void initialize() {
    }

    public void update(ChartContext<Integer, Double> chartContext, List<XYChart.Data<Number, Number>> data) {
        if(data!=null && !data.isEmpty()) {
            vnaStatistics.update(data);
            minSwr.setText(chartContext.format(vnaStatistics.getMinSwr()));
            maxSwr.setText(chartContext.format(vnaStatistics.getMaxSwr()));
            avgSwr.setText(chartContext.format(vnaStatistics.getAvgSwr()));
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
