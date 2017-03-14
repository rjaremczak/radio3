package com.mindpart.radio3;

import org.apache.commons.math3.complex.Complex;
import org.apache.commons.math3.complex.ComplexUtils;

/**
 * Created by Robert Jaremczak
 * Date: 2017.03.13
 */
public class VnaResult {
    private static final Complex sourceImpedance = Complex.valueOf(50.0);

    private double swr;
    private Complex impedance;

    public VnaResult(double returnLoss, double phaseDiff) {
        double gammaMagnitude = Math.pow(10, - returnLoss / 20);
        double gammaTheta = Math.toRadians(phaseDiff);
        Complex gamma = ComplexUtils.polar2Complex(gammaMagnitude, gammaTheta);

        swr = (1+gammaMagnitude)/(1-gammaMagnitude);
        impedance = sourceImpedance.multiply(Complex.ONE.add(gamma).divide(Complex.ONE.subtract(gamma)));
    }

    public double getSwr() {
        return swr;
    }

    public double getR() {
        return impedance.getReal();
    }

    public double getX() {
        return impedance.getImaginary();
    }

    public double getZ() {
        return Math.sqrt(impedance.getReal()*impedance.getReal() + impedance.getImaginary()*impedance.getImaginary());
    }

    public Complex getImpedance() {
        return impedance;
    }
}
