package com.obectron.exception;

import java.io.Serial;

public class UnexpectedEOFException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public UnexpectedEOFException(String unexpectedEofWhileReading) {
    }

}
