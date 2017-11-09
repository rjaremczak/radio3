package com.mindpart.type;

/**
 * Created by Robert Jaremczak
 * Date: 2017.10.30
 */
public class Inductance extends Quantity {
    public Inductance(double henry) {
        super(henry);
    }

    @Override
    public Unit getUnit() {
        return Unit.HENRY;
    }
}
