package com.mindpart.numeric;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.12
 */
public class MinCheck extends ValueCheck {
    @Override
    public void sample(int number, double value) {
        if(!isFound() || this.sampleValue > value) {
            this.sampleValue = value;
            this.sampleNumber = number;
        }
    }
}
