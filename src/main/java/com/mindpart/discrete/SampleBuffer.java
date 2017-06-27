package com.mindpart.discrete;

import org.apache.commons.collections4.queue.CircularFifoQueue;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by Robert Jaremczak
 * Date: 2014-10-22
 */
public class SampleBuffer {
    private CircularFifoQueue<Sample> values;

    public SampleBuffer(Collection<Sample> samples) {
        values = new CircularFifoQueue<>(samples);
    }

    public int size() {
        return values.size();
    }

    public int maxSize() {
        return values.maxSize();
    }

    public boolean isEmpty() {
        return values.isEmpty();
    }

    public boolean contains(Object o) {
        return values.contains(o);
    }

    private double calculateAverage(Collection<Double> data) {
        double avg = 0;
        for(double val : data) {
            avg += val;
        }
        return avg/data.size();
    }

    public SampleBuffer smooth(int avgSize) {
        List<Sample> result = new ArrayList<>(values.size());
        CircularFifoQueue<Double> avgbuf = new CircularFifoQueue<>(avgSize);
        for(Sample sample : values) {
            avgbuf.add(sample.getValue());
            result.add(new Sample(sample.getNumber(), calculateAverage(avgbuf)));
        }
        return new SampleBuffer(result);
    }
}
