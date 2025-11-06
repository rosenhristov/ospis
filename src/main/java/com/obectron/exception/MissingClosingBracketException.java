package com.obectron.exception;

import java.io.Serial;

public class MissingClosingBracketException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public MissingClosingBracketException() {
        super("Missing closing bracket ')'");
    }
    public MissingClosingBracketException(String s) {
        super(s);
    }
}
