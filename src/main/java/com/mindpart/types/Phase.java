package com.mindpart.types;

/**
 * Created by Robert Jaremczak
 * Date: 2016.11.09
 */
public class Phase {
    private int angle;

    public Phase(int angle) {
        this.angle = angle;
    }

    public Phase(double angle) {
        this.angle = (int) angle;
    }

    public String format() {
        return Integer.toString(angle);
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
