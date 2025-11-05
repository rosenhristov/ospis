package com.obectron;

import static com.obectron.Constants.EMPTY_STRING;
import static com.obectron.Constants.SPACE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Cons implements LispObject {

    private LispObject car;
    private LispObject cdr;

    private Cons(LispObject car, LispObject cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    public static Cons of(LispObject car, LispObject cdr) {
        return new Cons(car, cdr);
    }

    public LispObject car() {
        return car;
    }

    public LispObject cdr() {
        return cdr;
    }

    public String toString() {
        return String.format("(%s)", toStr(this));
    }

    private String toStr(LispObject obj) {
        if (isNull(obj)) {
            return EMPTY_STRING;
        }

        if (obj instanceof Cons c) {
            return c.car()
                    + (nonNull(c.cdr())
                        ? SPACE + toStr(c.cdr())
                        : EMPTY_STRING);
        }
        return obj.toString();
    }

    public void setCar(LispObject cell) {
        this.car = cell;
    }

    public void setCdr(LispObject cell) {
        this.cdr = cell;
    }
}
