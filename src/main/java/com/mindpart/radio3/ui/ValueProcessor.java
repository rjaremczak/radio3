package com.mindpart.radio3.ui;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.02
 */
interface ValueProcessor<R,T> {
    String axisLabel();
    String valueLabel();
    T parse(R rawData);
    String format(T value);
    T process(int arg, T value);
}
