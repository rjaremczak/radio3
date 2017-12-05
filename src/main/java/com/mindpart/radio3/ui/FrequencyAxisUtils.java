package com.mindpart.radio3.ui;

import com.mindpart.science.Frequency;
import com.mindpart.science.UnitPrefix;
import javafx.scene.chart.NumberAxis;

import static com.mindpart.science.UnitPrefix.MEGA;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.11
 */
public final class FrequencyAxisUtils {
    private FrequencyAxisUtils() {}

    private static double autoTickUnit(double valueSpan) {
        for (double div = 0.000001; div <= 100; div *= 10) {
            if (valueSpan < div) {
                return div / 25;
            }
        }
        return 1.0;
    }

    public static void setupFrequencyAxis(NumberAxis axis, Frequency freqStart, Frequency freqEnd) {
        double f0 = freqStart.to(MEGA);
        double f1 = freqEnd.to(MEGA);
        axis.setAutoRanging(false);
        axis.setLowerBound(f0);
        axis.setUpperBound(f1);
        axis.setTickUnit(autoTickUnit(f1 - f0));
        axis.setForceZeroInRange(false);
    }

}
