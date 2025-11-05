package com.obectron;

public enum Bool {
    TRUE("true"), NIL("nil");
    private final String value;

    private Bool(String value) {
        this.value = value;
    }
}
