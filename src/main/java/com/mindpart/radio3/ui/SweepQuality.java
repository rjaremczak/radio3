package com.mindpart.radio3.ui;

/**
 * Created by Robert Jaremczak
 * Date: 2017.03.15
 */
public enum SweepQuality {
    BEST("best", 1000, 2, 2),
    FINE("fine", 750, 2, 2),
    BALANCED("balanced", 500, 2, 2),
    FAST("fast", 250, 2, 2),
    FASTEST("fastest", 100, 1, 1);

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
