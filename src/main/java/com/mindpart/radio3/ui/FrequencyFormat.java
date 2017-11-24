package com.mindpart.radio3.ui;

import com.mindpart.type.UnitPrefix;

import java.text.DecimalFormat;
import java.text.NumberFormat;

/**
 * Created by Robert Jaremczak
 * Date: 2017.11.17
 */
public class FrequencyFormat {
    private static final ThreadLocal<NumberFormat> FORMATTER_FREQ_SHORT = ThreadLocal.withInitial(() -> new DecimalFormat("0.000"));
    private static final ThreadLocal<NumberFormat> FORMATTER_FREQ_EXACT = ThreadLocal.withInitial(() -> new DecimalFormat("0.000000"));

    public String formatShort(double value, UnitPrefix unitPrefix) {
        return FORMATTER_FREQ_SHORT.get().format(UnitPrefix.MEGA.from(value, unitPrefix));
    }

    public String format(double mhz) {
        return FORMATTER_FREQ_EXACT.get().format(mhz);
    }

    public String format(int hz) {
        return format(UnitPrefix.MEGA.fromBase(hz));
    }
}
