package com.obectron.env;

import com.obectron.exception.NotAFunctionException;
import com.obectron.functions.Defn;
import com.obectron.functions.Fn;
import com.obectron.primitives.*;
import com.obectron.primitives.Number;

import java.util.HashMap;
import java.util.Map;

import static com.obectron.primitives.Bool.NIL;
import static com.obectron.primitives.Bool.TRUE;
import static com.obectron.common.Constants.*;
import static java.util.Objects.nonNull;

public record Env(Map<String, OspisObject> vars,
                  Env parent) {

    public OspisObject lookup(String name) {
        if (name.startsWith(":")) {
            return Keyword.of(name);
        }
        if (vars.containsKey(name)) {
            return vars.get(name);
        }
        if (nonNull(parent)) {
            return parent.lookup(name);
        }
        return Atom.of(name); // free variable
    }

    public void define(String name, OspisObject value) {
        vars.put(name, value);
    }

    public static OspisObject eval(OspisObject expr, Env env) {
        if (expr instanceof Atom atom) {
            return env.lookup(atom.getValue());
        }

        if (!(expr instanceof Cons list)) {
            return expr;
        }

        OspisObject operation = list.car();
        if (!(operation instanceof Atom)) {
            OspisObject fn = eval(operation, env);
            OspisObject args = evalList((Cons) list.cdr(), env);
            return apply(fn, args);
        }

        String command = ((Atom) list.car()).getValue();

        return switch (command) {
            case QUOTE -> ((Cons) list.cdr()).car();
            case ATOM  -> atom(eval(((Cons) list.cdr()).car(), env));
            case EQ    -> eq(eval(((Cons) list.cdr()).car(), env),
                             eval(((Cons) ((Cons) list.cdr()).cdr()).car(), env));
            case CAR   -> ((Cons) eval(((Cons) list.cdr()).car(), env)).car();
            case CDR   -> ((Cons) eval(((Cons) list.cdr()).car(), env)).cdr();
            case CONS  -> Cons.of(eval(((Cons) list.cdr()).car(), env),
                                  eval(((Cons) ((Cons) list.cdr()).cdr()).car(), env));
            case COND  -> evalCond((Cons) list.cdr(), env);
            case FN -> Fn.parse(list, env);
            case DEFN -> Defn.parse(list, env);
            case PLUS, MINUS, MULTIPLY, DIVIDE -> mathOperation(command, (Cons) list.cdr(), env);

            default -> apply(eval(list.car(), env), evalList(((Cons) list.cdr()), env));
        };
    }

    public static OspisObject apply(OspisObject fn, OspisObject args) {
        if (fn instanceof Fn lambda) {
            Map<String, OspisObject> localVars = new HashMap<>();
            OspisObject paramList = lambda.getParams();
            OspisObject argList = args;
            while (nonNull(paramList) && nonNull(argList)) {
                Atom param = (Atom) ((Cons) paramList).car();
                OspisObject val = ((Cons) argList).car();
                localVars.put(param.getValue(), val);
                paramList = ((Cons) paramList).cdr();
                argList = ((Cons) argList).cdr();
            }
            return eval(lambda.getBody(), new Env(localVars, lambda.getLexicalScope()));
        }
        throw new NotAFunctionException("Not a function: " + fn);
    }

    public static OspisObject evalList(Cons list, Env env) {
        return nonNull(list)
                ? Cons.of(eval(list.car(), env),
                          evalList((Cons) list.cdr(), env))
                : null;
    }

    public static OspisObject evalCond(Cons clauses, Env env) {
        for (OspisObject cons = clauses; nonNull(cons); cons = ((Cons) cons).cdr()) {
            Cons clause = (Cons) ((Cons) cons).car();
            OspisObject evaluation = eval(clause.car(), env);
            if (nonNull(evaluation) && !NIL.name().equals(evaluation.toString())) {
                return eval(((Cons) clause.cdr()).car(), env);
            }
        }
        return Atom.of(NIL.name());
    }

    public static OspisObject atom(OspisObject x) {
        return x.isAtom() ? Atom.of(TRUE.name()) : Atom.of(NIL.name());
    }

    public static OspisObject eq(OspisObject paramA, OspisObject paramB) {
        if (paramA instanceof Atom atomA
                && paramB instanceof Atom atomB
                && atomA.getValue().equals(atomB.getValue())) {
            return Atom.of(TRUE.name());
        }
        return Atom.of(NIL.name());
    }

    public static OspisObject list(OspisObject... elements) {
        OspisObject result = null;
        for (int i = elements.length - 1; i >= 0; i--) {
            result = Cons.of(elements[i], result);
        }
        return result;
    }

    public static OspisObject mathOperation(String operator, Cons args, Env env) {
        double result = ((Number) eval(args.car(), env)).getNumber();
        for (OspisObject rest = args.cdr(); nonNull(rest); rest = ((Cons) rest).cdr()) {
            double num = ((Number) eval(((Cons) rest).car(), env)).getNumber();
            switch (operator) {
                case "+" -> result += num;
                case "-" -> result -= num;
                case "*" -> result *= num;
                case "/" -> result /= num;
                default -> throw new IllegalArgumentException("Unknown operator: " + operator);
            }
        }
        return Number.of(Double.toString(result));
    }
}
