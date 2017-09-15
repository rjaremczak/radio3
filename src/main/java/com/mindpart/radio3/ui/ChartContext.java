package com.mindpart.radio3.ui;

/**
 * Created by Robert Jaremczak
 * Date: 2017.09.15
 */
class ChartContext {
    double[] receivedData;
    double[] processedData;
    double[] receivedFreq;
    ValueProcessor<Integer,Double> valueProcessor;

    void init(int length) {
        receivedData = new double[length];
        receivedFreq = new double[length];
        processedData = new double[length];
    }

    void clear() {
        receivedFreq = null;
        receivedData = null;
        processedData = null;
    }

    final boolean isReady() {
        return receivedData != null && receivedFreq != null;
    }

    public void setReceivedData(int step, int data, double freq) {
        receivedData[step] = valueProcessor.parse(data);
        receivedFreq[step] = freq;
    }

    double setAndGetProcessedData(int step) {
        double processed = valueProcessor.process(step, receivedData[step]);
        processedData[step] = processed;
        return processed;
    }

    int getDataSize() {
        return receivedData.length;
    }
}
