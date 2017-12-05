package com.mindpart.science;

/**
 * Created by Robert Jaremczak
 * Date: 2017.11.28
 */
public class Unit {
    private final String symbol;

    Unit(String symbol) {
        this.symbol = symbol;
    }

    public String getSymbol() {
        return symbol;
    }

    public String getSymbol(UnitPrefix prefix) {
        return prefix.getSymbol()+symbol;
    }
}
