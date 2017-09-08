package com.mindpart.radio3.ui;

/**
 * Created by Robert Jaremczak
 * Date: 2017.03.15
 */
public enum SweepQuality {
    BEST("%quality.best", 1000, 2, 2),
    BALANCED("%quality.balanced", 500, 2, 2),
    FAST("%quality.fast", 250, 1, 2);

    private String name;
    private int steps;
    private int avgPasses;
    private int avgSamples;

    SweepQuality(String name, int steps, int avgPasses, int avgSamples) {
        this.name = name;
        this.steps = steps;
        this.avgPasses = avgPasses;
        this.avgSamples = avgSamples;
    }

    public String getName() {
        return name;
    }

    public int getSteps() {
        return steps;
    }

    public int getAvgPasses() {
        return avgPasses;
    }

    public int getAvgSamples() {
        return avgSamples;
    }

    @Override
    public String toString() {
        return name;
    }
}
