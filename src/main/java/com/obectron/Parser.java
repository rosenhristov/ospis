package com.obectron;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static java.util.Objects.isNull;

public class Parser {

    private Parser() {}

    public LispObject parse(String code) {
        List<String> tokens = tokenize(code);
        if (tokens.isEmpty()) {
            return null;
        }
        return readFromTokens(tokens);
    }

    private List<String> tokenize(String code) {
        code = code.replace("(", " ( ").replace(")", " ) ");
        String[] tokens = code.trim().split("\\s+");
        return new ArrayList<>(Arrays.asList(tokens));
    }

    private LispObject readFromTokens(List<String> tokens) {
        if (tokens.isEmpty()) {
            throw new RuntimeException("Unexpected EOF while reading");
        }

        String token = tokens.removeFirst();
        if (token.equals("(")) {
            LispObject head = null;
            LispObject tail = null;

            while (!tokens.isEmpty() && !")".equals(tokens.get(0))) {
                LispObject elem = readFromTokens(tokens);
                Cons cell = Cons.of(elem, null);
                if (isNull(head)) {
                    head = cell;
                    tail = cell;
                } else {
                    ((Cons) tail).setCdr(cell);
                    tail = cell;
                }
            }

            if (tokens.isEmpty()) {
                throw new RuntimeException("Missing closing ')'");
            }

            tokens.removeFirst(); // removes ')'
            return head;
        } else if (token.equals(")")) {
            throw new RuntimeException("Unexpected ')'");
        } else {
            return Env.atomOrNumber(token);
        }
    }

    public static void main(String[] args) {
        Parser parser = new Parser();
        String src = "(defn square (x) (* x x))";
        LispObject expr = parser.parse(src);
        System.out.println(expr);

        Env env = new Env(new HashMap<>(), null);
        Env.eval(expr, env);

        LispObject call = parser.parse("(square 7)");
        LispObject result = Env.eval(call, env);

        System.out.println("Result: " + result);
    }
}
