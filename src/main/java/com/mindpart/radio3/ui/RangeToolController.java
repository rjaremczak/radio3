package com.mindpart.radio3.ui;

import com.mindpart.numeric.MaxCheck;
import com.mindpart.numeric.MinCheck;
import com.mindpart.type.Frequency;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.15
 */
public class RangeToolController {
    private static final NumberFormat FORMAT_FREQ = new DecimalFormat("0.000000");
    private static final NumberFormat FORMAT_VALUE = new DecimalFormat("0.0");

    private final TitledPane titledPane;
    private final PropertyGrid propertyGrid;
    private final Label minValue;
    private final Label minFreq;
    private final Label maxValue;
    private final Label maxFreq;
    private final Label spanValue;
    private final ChartContext chartContext;

    public RangeToolController(BundleData bundle, ChartContext chartContext) {
        this.chartContext = chartContext;

        propertyGrid = new PropertyGrid();
        minValue = propertyGrid.addProperty("min [dBm]");
        minFreq = propertyGrid.addProperty("f min [MHz]");
        propertyGrid.addRow();
        maxValue = propertyGrid.addProperty("max [dBm]");
        maxFreq = propertyGrid.addProperty("f max [MHz]");
        propertyGrid.addRow();
        spanValue = propertyGrid.addProperty("Δf [MHz]");

        titledPane = new TitledPane(bundle.resolve("info.ranges.title"), propertyGrid.getNode());
        titledPane.setAlignment(Pos.TOP_LEFT);
        titledPane.setAnimated(false);
    }

    public void clear() {
        minValue.setText("");
        minFreq.setText("");
        maxValue.setText("");
        maxFreq.setText("");
        spanValue.setText("");
    }

    public void update() {
        MinCheck minCheck = new MinCheck();
        MaxCheck maxCheck = new MaxCheck();

        for(int i=0; i<chartContext.getDataSize(); i++) {
            minCheck.sample(i, chartContext.processedData[i]);
            maxCheck.sample(i, chartContext.processedData[i]);
        }

        minValue.setText(FORMAT_VALUE.format(minCheck.getSampleValue()));
        minFreq.setText(FORMAT_FREQ.format(chartContext.receivedFreq[minCheck.getSampleNumber()]));
        maxValue.setText(FORMAT_VALUE.format(maxCheck.getSampleValue()));
        maxFreq.setText(FORMAT_FREQ.format(chartContext.receivedFreq[maxCheck.getSampleNumber()]));
        spanValue.setText(FORMAT_VALUE.format(maxCheck.getSampleValue() - minCheck.getSampleValue()));
    }

    public TitledPane getTitledPane() {
        return titledPane;
    }

}
