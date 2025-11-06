package com.obectron.primitives;

import static com.obectron.common.Constants.EMPTY_STRING;
import static com.obectron.common.Constants.SPACE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Cons implements OspisObject {

    private OspisObject car;
    private OspisObject cdr;

    private Cons(OspisObject car, OspisObject cdr) {
        this.car = car;
        this.cdr = cdr;
    }

    public static Cons of(OspisObject car, OspisObject cdr) {
        return new Cons(car, cdr);
    }

    public OspisObject car() {
        return car;
    }

    public OspisObject cdr() {
        return cdr;
    }

    public String toString() {
        return String.format("(%s)", toStr(this));
    }

    private String toStr(OspisObject obj) {
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

    public void setCar(OspisObject cell) {
        this.car = cell;
    }

    public void setCdr(OspisObject cell) {
        this.cdr = cell;
    }
}
