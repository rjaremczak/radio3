package com.mindpart.radio3.ui;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.02
 */
public interface ChartContext<R,T> {
    String label();
    T parse(R rawData);
    String format(T value);
    T process(int arg, T value);
}
