package com.mindpart.type;

/**
 * Created by Robert Jaremczak
 * Date: 2017.10.24
 */
public class Capacitance extends Quantity {

    public Capacitance(double farad) {
        super(farad);
    }

    @Override
    public Unit getUnit() {
        return Unit.FARAD;
    }
}
