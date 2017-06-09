package com.mindpart.radio3;

import com.mindpart.types.Frequency;
import javafx.scene.chart.XYChart;

import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.05
 */
public class SweepStatistics {
    private double minFrequency;
    private double maxFrequency;
    private double minValue;
    private double maxValue;

    public void update(List<XYChart.Data<Number, Number>> data) {
        minValue = Double.MAX_VALUE;
        maxValue = -Double.MAX_VALUE;

        for(XYChart.Data<Number, Number> item : data) {
            double freq = item.getXValue().doubleValue();
            double value = item.getYValue().doubleValue();

            if(value < minValue) {
                minValue = value;
                minFrequency = freq;
            }

            if(value > maxValue) {
                maxValue = value;
                maxFrequency = freq;
            }
        }
    }

    public Frequency getMinFrequency() {
        return Frequency.ofMHz(minFrequency);
    }

    public Frequency getMaxFrequency() {
        return Frequency.ofMHz(maxFrequency);
    }

    public double getMinValue() {
        return minValue;
    }

    public double getMaxValue() {
        return maxValue;
    }

    public double getSpanValue() {
        return Math.abs(maxValue - minValue);
    }
}