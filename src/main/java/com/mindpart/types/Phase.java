package com.mindpart.types;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.09
 */
public class Phase {
    private static final NumberFormat FORMAT_DEG = new DecimalFormat("0 °");

    private int angle;

    public Phase(int angle) {
        this.angle = angle;
    }

    public Phase(double angle) {
        this.angle = (int) angle;
    }

    public String format() {
        return FORMAT_DEG.format(angle);
    }

    public void parse(String str) {
        angle = Integer.parseInt(str);
    }

    public int getAngle() {
        return angle;
    }

    @Override
    public String toString() {
        return format();
    }
}
