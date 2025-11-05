package com.obectron;

public class NumberAtom extends Atom {

    final double number;

    private NumberAtom(String value) {
        super(value);
        this.number = Double.parseDouble(value);
    }

    public static NumberAtom of(String value) {
        return new NumberAtom(value);
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
