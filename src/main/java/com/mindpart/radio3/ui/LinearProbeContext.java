package com.mindpart.radio3.ui;

import com.mindpart.types.Voltage;

import java.util.function.Function;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.03
 */
public class LinearProbeContext implements ChartContext<Integer, Double> {
    private Function<Integer,Double> valueParser;

    public LinearProbeContext(Function<Integer,Double> valueParser) {
        this.valueParser = valueParser;
    }

    @Override
    public String label() {
        return "Voltage [V]";
    }

    @Override
    public Double parse(Integer rawData) {
        return valueParser.apply(rawData);
    }

    @Override
    public String format(Double value) {
        return "voltage: "+ Voltage.ofVolt(value).format();
    }

    @Override
    public Double process(int sampleNum, Double value) {
        return value;
    }
}
