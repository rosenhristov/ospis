package com.obectron.exception;

public class UnexpectedClosingBracket extends RuntimeException {

    public UnexpectedClosingBracket() {
        super("Unexpected ')'");
    }
    public UnexpectedClosingBracket(String s) {
        super(s);
    }
}
