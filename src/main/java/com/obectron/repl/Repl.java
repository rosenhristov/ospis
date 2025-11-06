package com.obectron.repl;

import com.obectron.env.Env;
import com.obectron.primitives.OspisObject;
import com.obectron.parser.Parser;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.obectron.common.Constants.CLOSE_BRACKET;
import static com.obectron.common.Constants.OPEN_BRACKET;

public class Repl {

    private final Env globalEnv;

    public Repl() {
        this.globalEnv = new Env(new HashMap<>(), null);
    }

    public Repl(Env globalEnv) {
        this.globalEnv = globalEnv;
    }

    public void runFile(String filePath) {
        try {
            String content = Files.readString(Path.of(filePath));
            runProgram(content);
        } catch (IOException e) {
            System.err.println("Error reading file: " + e.getMessage());
        }
    }

    public void runProgram(String code) {
        List<String> expressions = splitExpressions(code);
        for (String expr : expressions) {
            if (expr.isBlank()) {
                continue;
            }
            try {
                OspisObject parsed = Parser.parse(expr);
                OspisObject result = Env.eval(parsed, globalEnv);
                System.out.println(":obectron>> " + result);
            } catch (Exception e) {
                System.err.println("Error: " + e.getMessage());
            }
        }
    }

    private static List<String> splitExpressions(String code) {
        List<String> result = new ArrayList<>();
        StringBuilder expression = new StringBuilder();
        int depth = 0;
        for (char c : code.toCharArray()) {
            depth = checkDepth(c, depth);
            expression.append(c);
            if (depth == 0 && isCompleteList(expression)) {
                result.add(expression.toString().trim());
                expression.setLength(0);
            }
        }
        if (!expression.isEmpty()) {
            result.add(expression.toString().trim());
        }
        return result;
    }

    private static boolean isCompleteList(StringBuilder current) {
        return startsAsList(current) && endsAsList(current);
    }

    private static boolean startsAsList(StringBuilder current) {
        return current.toString().trim().startsWith(String.valueOf(OPEN_BRACKET));
    }

    private static boolean endsAsList(StringBuilder current) {
        return current.toString().trim().endsWith(String.valueOf(CLOSE_BRACKET));
    }

    public void interactive() {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(System.in))) {
            System.out.println("Ospis REPL - type 'exit' to quit:");
            while (true) {
                System.out.print(":obectron>> ");
                String line = reader.readLine();
                if (exitPrompted(line)) {
                    break;
                }
                if (line.isBlank()) {
                    continue;
                }
                try {
                    OspisObject parsed = Parser.parse(line);
                    OspisObject result = Env.eval(parsed, globalEnv);
                    System.out.println(":obectron>> " + result);
                } catch (Exception e) {
                    System.err.println("Error: " + e.getMessage());
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static int checkDepth(char c, int depth) {
        if (c == OPEN_BRACKET) depth++;
        if (c == CLOSE_BRACKET) depth--;
        return depth;
    }

    private static boolean exitPrompted(String line) {
        return line == null || line.equalsIgnoreCase("exit");
    }
}
