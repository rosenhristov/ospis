package com.obectron;

public class Fn implements LispObject {

    private final LispObject params;
    private final LispObject body;

    private final Env lexicalEnv;
    private Fn(LispObject params, LispObject body, Env lexicalEnv) {
        this.params = params;
        this.body = body;
        this.lexicalEnv = lexicalEnv;
    }

    public static Fn of(LispObject params, LispObject body, Env lexicalEnv) {
        return new Fn(params, body, lexicalEnv);
    }

    public LispObject getParams() {
        return params;
    }

    public LispObject getBody() {
        return body;
    }

    public Env getLexicalEnv() {
        return lexicalEnv;
    }

    public String toString() {
        return String.format("(fn %s -> %s)", params, body);
    }

}
