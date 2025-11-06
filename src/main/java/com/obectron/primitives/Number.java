package com.obectron.primitives;

public class Number extends Atom {

    final double number;

    private Number(String value) {
        super(value);
        this.number = Double.parseDouble(value);
    }

    public static Number of(String value) {
        return new Number(value);
    }

    public String toString() {
        if (number == (long) number) {
            return Long.toString((long) number);
        }
        return Double.toString(number);
    }

    public double getNumber() {
        return this.number;
    }
}
