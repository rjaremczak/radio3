package com.mindpart.radio3.ui;

import org.apache.commons.math3.complex.Complex;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Robert Jaremczak
 * Date: 2017.11.24
 */
public class ImpedanceFormat {
    private static final ThreadLocal<NumberFormat> FORMATTER = ThreadLocal.withInitial(() -> new DecimalFormat("0.0"));

    public String format(Complex impedance) {
        NumberFormat formatter = FORMATTER.get();
        return formatter.format(impedance.getReal())+" + j"+formatter.format(impedance.getImaginary());
    }
}
