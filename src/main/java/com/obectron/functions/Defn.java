package com.obectron.functions;

import com.obectron.env.Env;
import com.obectron.primitives.Atom;
import com.obectron.primitives.Cons;
import com.obectron.primitives.OspisObject;

public class Defn implements OspisObject {

    private final String name;
    private final OspisObject params;
    private final OspisObject body;
    private final Env lexicalScope;

    private Defn(String name, OspisObject params, OspisObject body, Env lexicalScope) {
        this.name = name;
        this.params = params;
        this.body = body;
        this.lexicalScope = lexicalScope;
    }

    public static Defn of(String name, OspisObject params, OspisObject body, Env lexicalEnv) {
        return new Defn(name, params, body, lexicalEnv);
    }

    public static OspisObject parse(Cons list, Env lexicalScope) {
        String name = parseName(list);
        OspisObject args = parseArgs(list);
        OspisObject body = parseBody(list);
        Defn namedFn = Defn.of(name, args, body, lexicalScope);
        lexicalScope.define(name, namedFn);
        return namedFn;
    }

    private static String parseName(Cons list) {
        return ((Atom) ((Cons) list.cdr()).car()).getValue();
    }
    private static OspisObject parseArgs(Cons list) {
        return ((Cons) ((Cons) list.cdr()).cdr()).car();
    }

    private static OspisObject parseBody(Cons list) {
        return ((Cons) ((Cons) ((Cons) list.cdr()).cdr()).cdr()).car();
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

    @Override
    public String toString() {
        return String.format("(defn %s [%s] (%s))", name, params, body);
    }
}
