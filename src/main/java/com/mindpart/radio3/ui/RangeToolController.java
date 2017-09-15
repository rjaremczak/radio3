package com.mindpart.radio3.ui;

import com.mindpart.numeric.MaxCheck;
import com.mindpart.numeric.MinCheck;
import com.mindpart.types.Frequency;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.TitledPane;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.15
 */
public class RangeToolController {
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
        minValue = propertyGrid.addProperty("min");
        minFreq = propertyGrid.addProperty(bundle.resolve("info.ranges.freq"));
        maxValue = propertyGrid.addProperty("max");
        maxFreq = propertyGrid.addProperty(bundle.resolve("info.ranges.freq"));
        spanValue = propertyGrid.addProperty(bundle.resolve("info.ranges.range"));

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

        minValue.setText(chartContext.valueProcessor.format(minCheck.getSampleValue()));
        minFreq.setText(Frequency.ofMHz(chartContext.receivedFreq[minCheck.getSampleNumber()]).format());
        maxValue.setText(chartContext.valueProcessor.format(maxCheck.getSampleValue()));
        maxFreq.setText(Frequency.ofMHz(chartContext.receivedFreq[maxCheck.getSampleNumber()]).format());
        spanValue.setText(chartContext.valueProcessor.format(maxCheck.getSampleValue() - minCheck.getSampleValue()));
    }

    public TitledPane getTitledPane() {
        return titledPane;
    }

}
