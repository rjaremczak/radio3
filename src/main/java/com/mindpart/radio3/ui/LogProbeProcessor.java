package com.mindpart.radio3.ui;

import com.mindpart.science.Power;

import java.util.function.Function;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.03
 */
public class LogProbeProcessor implements ValueProcessor<Integer, Double> {
    private final Function<Integer, Double> parser;
    private final String axisLabel;

    public LogProbeProcessor(Function<Integer, Double> parser, String axisLabel) {
        this.parser = parser;
        this.axisLabel = axisLabel;
    }

    @Override
    public String axisLabel() {
        return axisLabel;
    }

    @Override
    public String valueLabel() {
        return "P";
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
