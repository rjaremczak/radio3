package com.mindpart.type;

/**
 * Created by Robert Jaremczak
 * Date: 2017.10.30
 */
public abstract class Quantity implements Comparable<Quantity> {
    public enum Unit {
        VOLT("V"), OHM("Ω"), FARAD("F"), HENRY("H"), HERTZ("Hz");

        private final String symbol;

        Unit(String symbol) {
            this.symbol = symbol;
        }

        public String getSymbol() {
            return symbol;
        }

        public String getSymbol(MetricPrefix prefix) {
            return prefix.getSymbol()+symbol;
        }
    }

    private final double value;

    public Quantity(double value) {
        this.value = value;
    }

    public double getValue() {
        return value;
    }

    @Override
    public int compareTo(Quantity q) {
        return Double.compare(value, q.value);
    }

    public abstract Unit getUnit();
}
