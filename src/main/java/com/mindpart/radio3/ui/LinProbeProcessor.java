package com.mindpart.radio3.ui;

import com.mindpart.science.Voltage;

import java.util.function.Function;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.03
 */
public class LinProbeProcessor implements ValueProcessor<Integer, Double> {
    private Function<Integer,Double> valueParser;
    private final String axisLabel;

    public LinProbeProcessor(Function<Integer,Double> valueParser, String axisLabel) {
        this.valueParser = valueParser;
        this.axisLabel = axisLabel;
    }

    @Override
    public String axisLabel() {
        return axisLabel;
    }

    @Override
    public String valueLabel() {
        return "V";
    }

    @Override
    public Double parse(Integer rawData) {
        return valueParser.apply(rawData);
    }

    @Override
    public String format(Double value) {
        return Voltage.ofVolt(value).format();
    }

    @Override
    public Double process(int sampleNum, Double value) {
        return value;
    }
}
