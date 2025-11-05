package com.obectron;

import java.util.HashMap;
import java.util.Map;

import static com.obectron.Bool.NIL;
import static com.obectron.Bool.TRUE;
import static com.obectron.Constants.*;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public record Env(Map<String, LispObject> vars,
                  Env parent) {

    public LispObject lookup(String name) {
        if (vars.containsKey(name)) {
            return vars.get(name);
        }
        if (nonNull(parent)) {
            return parent.lookup(name);
        }
        return Atom.of(name); // free variable
    }

    public void define(String name, LispObject value) {
        vars.put(name, value);
    }

    public static LispObject eval(LispObject expr, Env env) {
        if (expr instanceof Atom atom) {
            return env.lookup(atom.getValue());
        }

        if (!(expr instanceof Cons list)) {
            return expr;
        }

        LispObject operation = list.car();
        if (!(operation instanceof Atom)) {
            LispObject fn = eval(operation, env);
            LispObject args = evalList((Cons) list.cdr(), env);
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
            case FN -> Fn.of(((Cons) list.cdr()).car(),
                             ((Cons) ((Cons) list.cdr()).cdr()).car(),
                             env);
            case DEFN -> defn(list, env);
            case PLUS, MINUS, MULTIPLY, DIVIDE -> mathOperation(command, (Cons) list.cdr(), env);

            default -> apply(eval(list.car(), env), evalList(((Cons) list.cdr()), env));
        };
    }

    public static LispObject apply(LispObject fn, LispObject args) {
        if (fn instanceof Fn lambda) {
            Map<String, LispObject> localVars = new HashMap<>();
            LispObject paramList = lambda.getParams();
            LispObject argList = args;
            while (nonNull(paramList) && nonNull(argList)) {
                Atom param = (Atom) ((Cons) paramList).car();
                LispObject val = ((Cons) argList).car();
                localVars.put(param.getValue(), val);
                paramList = ((Cons) paramList).cdr();
                argList = ((Cons) argList).cdr();
            }
            return eval(lambda.getBody(), new Env(localVars, lambda.getLexicalEnv()));
        }
        throw new NotAFunctionException("Not a function: " + fn);
    }

    public static LispObject evalList(Cons list, Env env) {
        return nonNull(list)
                ? Cons.of(eval(list.car(), env),
                          evalList((Cons) list.cdr(), env))
                : null;
    }

    private static Atom defn(Cons list, Env env) {
        String name = ((Atom) ((Cons) list.cdr()).car()).getValue();
        LispObject args = ((Cons) ((Cons) list.cdr()).cdr()).car();
        LispObject body = ((Cons) ((Cons) ((Cons) list.cdr()).cdr()).cdr()).car();
        Fn fn = Fn.of(args, body, env);
        env.define(name, fn);
        return Atom.of(name);
    }

    public static LispObject evalCond(Cons clauses, Env env) {
        for (LispObject cons = clauses; nonNull(cons); cons = ((Cons) cons).cdr()) {
            Cons clause = (Cons) ((Cons) cons).car();
            LispObject evaluation = eval(clause.car(), env);
            if (nonNull(evaluation) && !NIL.name().equals(evaluation.toString())) {
                return eval(((Cons) clause.cdr()).car(), env);
            }
        }
        return Atom.of(NIL.name());
    }

    public static LispObject atom(LispObject x) {
        return (isNull(x) || isAtom(x))
                ? Atom.of(TRUE.name())
                : Atom.of(NIL.name());
    }

    public static LispObject eq(LispObject paramA, LispObject paramB) {
        if (paramA instanceof Atom atomA
                && paramB instanceof Atom atomB
                && atomA.getValue().equals(atomB.getValue())) {
            return Atom.of(TRUE.name());
        }
        return Atom.of(NIL.name());
    }

    public static LispObject list(LispObject... elements) {
        LispObject result = null;
        for (int i = elements.length - 1; i >= 0; i--) {
            result = Cons.of(elements[i], result);
        }
        return result;
    }

    public static LispObject mathOperation(String operator, Cons args, Env env) {
        double result = ((NumberAtom) eval(args.car(), env)).getNumber();
        for (LispObject rest = args.cdr(); nonNull(rest); rest = ((Cons) rest).cdr()) {
            double num = ((NumberAtom) eval(((Cons) rest).car(), env)).getNumber();
            switch (operator) {
                case "+" -> result += num;
                case "-" -> result -= num;
                case "*" -> result *= num;
                case "/" -> result /= num;
                default -> throw new IllegalArgumentException("Unknown operator: " + operator);
            }
        }
        return NumberAtom.of(Double.toString(result));
    }

    public static Atom atomOrNumber(String s) {
        return s.matches("-?\\d+(\\.\\d+)?")
                ? NumberAtom.of(s)
                : Atom.of(s);
    }

    public static boolean isAtom(LispObject x) {
        return x instanceof Atom;
    }

}
