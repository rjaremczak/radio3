package com.mindpart.ui;

import com.mindpart.discrete.Range;
import javafx.scene.chart.NumberAxis;

import static com.mindpart.math.MathUtils.ceil;
import static com.mindpart.math.MathUtils.tickUnit;
import static com.mindpart.math.MathUtils.floor;
import static java.lang.Math.*;

/**
 * Created by Robert Jaremczak
 * Date: 2017.05.18
 */
public final class FxChartUtils {
    public static final int NUM_TICKS = 10;

    private FxChartUtils() {}

    public static void rangeAxis(NumberAxis axis, double min, double max, double tickUnit) {
        axis.setAutoRanging(false);
        axis.setLowerBound(min);
        axis.setUpperBound(max);
        axis.setTickUnit(tickUnit);
        axis.setMinorTickVisible(true);
        axis.setMinorTickCount(5);
    }

    public static void rangeAxis(NumberAxis axis, Range range, double minSpan, double minLimit, double maxLimit, double minGrid) {
        final double dspan = minSpan / 2;
        if(range.isValid()) {
            double rangeMid = round(range.mid());
            double rangeMin = max(minLimit, range.span() < minSpan ? rangeMid - dspan : floor(range.min(), minGrid));
            double rangeMax = min(maxLimit,  range.span() < minSpan ? rangeMid + dspan : ceil(range.max(), minGrid));
            double tickUnit = tickUnit(rangeMax - rangeMin, NUM_TICKS);
            rangeAxis(axis, rangeMin, rangeMax, tickUnit);
        } else {
            axis.setAutoRanging(true);
            axis.setForceZeroInRange(false);
        }
    }

    public static void rangeAxis(NumberAxis axis, Range range, double minSpan, double minGrid) {
        rangeAxis(axis, range, minSpan, -Double.MAX_VALUE, Double.MAX_VALUE, minGrid);
    }
}
