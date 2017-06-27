package com.mindpart.discrete;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2017.06.27
 */
public class ExtremaFinder {
    private List<Extremum> extrema = new ArrayList<>();
    private CircularFifoQueue<Sample> backlog = new CircularFifoQueue<>(2);

    public void reset() {
        extrema.clear();
        backlog.clear();
    }

    public void record(int number, double value) {
        if(backlog.size()>=2) {
            Sample mid = backlog.get(1);
        }
        backlog.add(new Sample(number, value));
    }

    public List<Extremum> getExtrema() {
        return extrema;
    }
}
