package com.mindpart.discrete;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
public class MaxFinder extends SampleFinder {
    @Override
    public void record(int number, double value) {
        if(!isFound() || sample.getValue() < value) {
            sample = new Sample(number, value);
        }
    }
}
