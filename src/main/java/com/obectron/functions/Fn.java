package com.obectron.functions;

import com.obectron.env.Env;
import com.obectron.primitives.Cons;
import com.obectron.primitives.OspisObject;

public class Fn implements OspisObject {

    private final OspisObject params;
    private final OspisObject body;

    private final Env lexicalScope;
    private Fn(OspisObject params, OspisObject body, Env lexicalScope) {
        this.params = params;
        this.body = body;
        this.lexicalScope = lexicalScope;
    }

    public static Fn of(OspisObject params, OspisObject body, Env lexicalEnv) {
        return new Fn(params, body, lexicalEnv);
    }

    public OspisObject getParams() {
        return params;
    }

    public OspisObject getBody() {
        return body;
    }

    public Env getLexicalScope() {
        return lexicalScope;
    }


    public static OspisObject parse(Cons list, Env lexicalScope) {
        OspisObject args = parseArgs(list);
        OspisObject body = parseBody(list);
        return Fn.of(args, body, lexicalScope);
    }

    /**
     * (fn (x y) (+ x y)) args : cdr->car
     */
    private static OspisObject parseArgs(Cons list) {
        return ((Cons) list.cdr()).car();
    }

    /**
     * (fn (x y) (+ x y)) body : cdr->cdr->car
     */
    private static OspisObject parseBody(Cons list) {
        return ((Cons) ((Cons) list.cdr()).cdr()).car();
    }

    @Override
    public String toString() {
        return String.format("(fn [%s] (%s))", params, body);
    }

}
