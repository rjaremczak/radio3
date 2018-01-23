package com.mindpart.radio3.ui;

import java.text.NumberFormat;

/**
 * Created by Robert Jaremczak
 * Date: 2017.11.24
 */
public class DecimalFormat {
    private static final ThreadLocal<NumberFormat> FORMATTER_INTEGER = ThreadLocal.withInitial(() -> new java.text.DecimalFormat("0"));
    private static final ThreadLocal<NumberFormat> FORMATTER_DEFAULT = ThreadLocal.withInitial(() -> new java.text.DecimalFormat("0.000"));
    private static final ThreadLocal<NumberFormat> FORMATTER_HIGH_PRECISION = ThreadLocal.withInitial(() -> new java.text.DecimalFormat("0.000000"));

    public String formatInteger(double value) {
        return FORMATTER_INTEGER.get().format(value);
    }

    public String format(double value) {
        return FORMATTER_DEFAULT.get().format(value);
    }

    public String formatHighPrecision(double value) {
        return FORMATTER_HIGH_PRECISION.get().format(value);
    }
}
