package com.mindpart.type;

/**
 * Created by Robert Jaremczak
 * Date: 2017.10.30
 */
public class Resistance extends Quantity {
    public Resistance(double ohm) {
        super(ohm);
    }

    @Override
    public Unit getUnit() {
        return Unit.OHM;
    }
}
