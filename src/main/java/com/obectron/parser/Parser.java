package com.obectron.parser;

import com.obectron.exception.MissingClosingBracketException;
import com.obectron.exception.UnexpectedClosingBracket;
import com.obectron.exception.UnexpectedEOFException;
import com.obectron.primitives.*;
import com.obectron.env.Env;
import com.obectron.primitives.Number;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import static com.obectron.common.Constants.*;
import static java.util.Objects.isNull;

public class Parser {

    private Parser() {}

    public static OspisObject parse(String code) {
        Parser parser = new Parser();
        return parser.parseCode(code);
    }
    public OspisObject parseCode(String code) {
        List<String> tokens = tokenize(code);
        if (tokens.isEmpty()) {
            return null;
        }
        return readFromTokens(tokens);
    }

    private List<String> tokenize(String code) {
        if (isNull(code) || isNotLispStyleCode(code)) {
            return new ArrayList<>();
        }
        code = code.replace("(", " ( ").replace(")", " ) ");
        String[] tokens = code.trim().split("\\s+");
        return new ArrayList<>(Arrays.asList(tokens));
    }

    private boolean isNotLispStyleCode(String code) {
        return code.contains(String.valueOf(OPEN_BRACKET)) && code.contains(String.valueOf(CLOSE_BRACKET));
    }

    private OspisObject readFromTokens(List<String> tokens) {
        if (tokens.isEmpty()) {
            throw new UnexpectedEOFException("Unexpected EOF while reading");
        }

        String token = tokens.removeFirst();
        if (token.equals(String.valueOf(OPEN_BRACKET))) {
            Cons head = null;
            Cons tail = null;

            while (hasTokensToRead(tokens)) {
                OspisObject elem = readFromTokens(tokens);
                Cons cell = Cons.of(elem, null);
                if(isNull(head)) {
                    head = cell;
                } else {
                    tail.setCdr(cell);
                }
                tail = cell;
            }

            if (tokens.isEmpty()) {
                throw new MissingClosingBracketException();
            }

            tokens.removeFirst(); // removes ')'
            return head;
        } else if (token.equals(String.valueOf(CLOSE_BRACKET))) {
            throw new UnexpectedClosingBracket();
        } else {
            return readAtom(token);
        }
    }

    public static Atom readAtom(String s) {
        if (s.startsWith(COLON)) {
            return Keyword.of(s);
        } else if (isNumber(s)) {
            return Number.of(s);
        }
        return Atom.of(s);
    }

    private static boolean isNumber(String s) {
        return s.matches("-?\\d+(\\.\\d+)?");
    }

    public static void main(String[] args) {
        Parser parser = new Parser();
        String src = "(defn square (x) (* x x))";
        OspisObject expr = parser.parseCode(src);
        System.out.println(expr);

        Env env = new Env(new HashMap<>(), null);
        Env.eval(expr, env);

        OspisObject call = parser.parseCode("(square 7)");
        OspisObject result = Env.eval(call, env);
        System.out.println("Result: " + result);
    }

    private static boolean hasTokensToRead(List<String> tokens) {
        return !tokens.isEmpty() && isNotEndOfList(tokens);
    }

    private static boolean isNotEndOfList(List<String> tokens) {
        return !tokens.getFirst().equals(String.valueOf(CLOSE_BRACKET));
    }
}
