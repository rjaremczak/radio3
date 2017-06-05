package com.mindpart.radio3.ui;

import com.mindpart.types.Power;

import java.util.function.Function;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.03
 */
public class LogarithmicProbeContext implements ChartContext<Integer, Double> {
    private Function<Integer, Double> parser;

    public LogarithmicProbeContext(Function<Integer, Double> parser) {
        this.parser = parser;
    }

    @Override
    public String axisLabel() {
        return "Power [dBm]";
    }

    @Override
    public String valueLabel() {
        return "power";
    }

    @Override
    public Double parse(Integer rawData) {
        return parser.apply(rawData);
    }

    @Override
    public String format(Double value) {
        return Power.ofDBm(value).formatDBm();
    }

    @Override
    public Double process(int arg, Double value) {
        return value;
    }
}
