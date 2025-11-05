package com.obectron;

import java.util.HashMap;

import static com.obectron.BaseCommand.*;
import static com.obectron.Bool.NIL;
import static com.obectron.Bool.TRUE;
import static com.obectron.Constants.MINUS;
import static com.obectron.Constants.MULTIPLY;
import static com.obectron.Constants.PLUS;
import static com.obectron.Env.*;

public class Main {

    public static void main(String[] args) {
        Env env = new Env(new HashMap<>(), null);

        // (quote (a b c))
        var expr1 = list(Atom.of(QUOTE.name()), list(Atom.of("a"), Atom.of("b"), Atom.of("c")));
        System.out.println(eval(expr1, env)); // -> (a b c)

        // (atom 'a)
        var expr2 = list(Atom.of(ATOM.name()), list(Atom.of(QUOTE.name()), Atom.of("a")));
        System.out.println(eval(expr2, env)); // -> T

        // (eq 'a 'b)
        var expr3 = list(Atom.of(EQ.name()),
                                list(Atom.of(QUOTE.name()), Atom.of("a")),
                                list(Atom.of(QUOTE.name()), Atom.of("b")));
        System.out.println(eval(expr3, env)); // -> NIL

        // (cons 'a '(b c))
        var expr4 = list(Atom.of(CONS.name()),
                                list(Atom.of(QUOTE.name()), Atom.of("a")),
                                list(Atom.of(QUOTE.name()), list(Atom.of("b"), Atom.of("c"))));
        System.out.println(eval(expr4, env)); // -> (a b c)

        // (cond ((eq 'a 'a) 'yes) (T 'no))
        var expr5 = list(Atom.of(COND.name()),
                        list(list(
                                list(Atom.of(EQ.name()),
                                        list(Atom.of(QUOTE.name()), Atom.of("a")),
                                        list(Atom.of(QUOTE.name()), Atom.of("a"))),
                                list(Atom.of(QUOTE.name()), Atom.of("yes")))),
                list(list(Atom.of(TRUE.name()),
                        list(Atom.of(QUOTE.name()), Atom.of("no")))));
        System.out.println(eval(expr5, env)); // -> yes

        // (defun mypair (x y) (cons x (cons y NIL)))
        LispObject defn = list(Atom.of(DEFN.name()), Atom.of("mypair"),
                            list(Atom.of("x"), Atom.of("y")),
                            list(Atom.of(CONS.name()), Atom.of("x"),
                                    list(Atom.of(CONS.name()), Atom.of("y"), Atom.of(NIL.name()))));
        eval(defn, env);

        var call = list(Atom.of("mypair"),
                        list(Atom.of(QUOTE.name()), Atom.of("a")),
                        list(Atom.of(QUOTE.name()), Atom.of("b")));
        System.out.println(eval(call, env)); // -> (a b)

        // ((lambda (x) (cons x '(b c))) 'a)
        LispObject lambda = list(
                list(Atom.of("lambda"),
                        list(Atom.of("x")),
                        list(Atom.of(CONS.name()), Atom.of("x"),
                                list(Atom.of(QUOTE.name()), list(Atom.of("b"), Atom.of("c"))))),
                list(Atom.of(QUOTE.name()), Atom.of("a")));
        System.out.println(eval(lambda, env)); // -> (a b c)

        var sum = list(atomOrNumber("+"), atomOrNumber("2"), atomOrNumber("3"), atomOrNumber("4"));
        System.out.println(eval(sum, env)); // -> 9

        // (defn square (x) (* x x))
        var square = list(new Atom(DEFN.name()), new Atom("square"),
                            list(new Atom("x")),
                            list(new Atom(MULTIPLY), new Atom("x"), new Atom("x")));
        eval(square, env);

        // (square 7)
        var callSquare = list(Atom.of("square"), atomOrNumber("7"));
        System.out.println(eval(callSquare, env)); // -> 49

        // ((fn (x y) (+ x (* y 2))) 5 10)
        var lambdaExpr = list(
                            list(new Atom(FN.name()),
                                    list(Atom.of("x"), Atom.of("y")),
                                    list(Atom.of(PLUS), Atom.of("x"),
                                            list(Atom.of(MULTIPLY), Atom.of("y"), atomOrNumber("2")))),
                            atomOrNumber("5"),
                            atomOrNumber("10"));
        System.out.println(eval(lambdaExpr, env)); // -> 25


        // (defn fact (n) (cond ((eq n 0) 1) (true (* n (fact (- n 1))))))
        var defFact = list(Atom.of(DEFN.name()), Atom.of("fact"), list(Atom.of("n")),
                            list(Atom.of(COND.name()),
                                    list(list(Atom.of(EQ.name()), Atom.of("n"), atomOrNumber("0")),
                                              atomOrNumber("1")),
                                    list(list(Atom.of(TRUE.name())),
                                            list(Atom.of(MULTIPLY), Atom.of("n"),
                                                    list(Atom.of("fact"),
                                                            list(Atom.of(MINUS), Atom.of("n"), atomOrNumber("1")))))));
        eval(defFact, env);

        var callFact = list(Atom.of("fact"), atomOrNumber("5"));
        System.out.println(eval(callFact, env)); // -> 120
    }

}