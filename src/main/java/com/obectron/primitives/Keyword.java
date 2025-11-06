package com.obectron.primitives;

import static com.obectron.common.Constants.COLON;

public class Keyword extends Atom {

    private static final String NOT_A_KEYWORD_ERROR = "Keyword must start with ':'. Expected: ':%s', actual: '%s'";

    private Keyword(String value) {
        super(value);
    }

    public static Keyword of(String value) {
        if (!value.startsWith(COLON)) {
            throw new IllegalArgumentException(String.format(NOT_A_KEYWORD_ERROR, value, value));
        }
        return new Keyword(value);
    }

    @Override
    public String toString() {
        return getValue();
    }
}
