package com.obectron.primitives;

public class Atom implements OspisObject {

    private final String value;

    Atom() {
        this.value = null;
    }

    Atom(String value) {
        this.value = value;
    }

    public static Atom of(String name) {
        return new Atom(name);
    }

    public String getValue() {
        return value;
    }

    public String toString() {
        return value;
    }
}
