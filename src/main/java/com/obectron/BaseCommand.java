package com.obectron;

public enum BaseCommand {
    QUOTE("quote"),
    ATOM("atom"),
    CAR("car"),
    CDR("cdr"),
    CONS("cons"),
    EQ("eq"),
    COND("cond"),
    FN("fn"),
    DEFN("defn");

    private final String name;

    private BaseCommand(String name) {
        this.name = name;
    }

}
