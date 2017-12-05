package com.mindpart.radio3;

import com.mindpart.science.Frequency;
import com.mindpart.science.UnitPrefix;
import javafx.scene.chart.XYChart;

import java.util.List;

import static com.mindpart.science.UnitPrefix.MEGA;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.05
 */
public class VnaStatistics {
    private double minFrequency;
    private double maxFrequency;
    private double minSwr;
    private double maxSwr;
    private double avgSwr;

    public void update(List<XYChart.Data<Number, Number>> data) {
        minSwr = Double.MAX_VALUE;
        maxSwr = -Double.MAX_VALUE;

        for(XYChart.Data<Number, Number> item : data) {
            double freq = item.getXValue().doubleValue();
            double value = item.getYValue().doubleValue();

            if(value < minSwr) {
                minSwr = value;
                minFrequency = freq;
            }

            if(value > maxSwr) {
                maxSwr = value;
                maxFrequency = freq;
            }
        }
    }

    public Frequency getMinFrequency() {
        return new Frequency(minFrequency, MEGA);
    }

    public Frequency getMaxFrequency() {
        return new Frequency(maxFrequency, MEGA);
    }

    public double getMinSwr() {
        return minSwr;
    }

    public double getMaxSwr() {
        return maxSwr;
    }

    public double getAvgSwr() {
        return avgSwr;
    }
}