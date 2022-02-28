package com.epam.crs.parsing;

import java.util.ArrayList;
import java.util.List;

public class ExpressionCalculator {
    
    public int calculate(String expression) {
        checkExpression(expression);
        List<Lexeme> lexemes = lexemesAnalyze(expression);
        LexemeBuffer lexemeBuffer = new LexemeBuffer(lexemes);
        return calcExpr(lexemeBuffer);
    }

    private void checkExpression(String expression) {
        checkCharacters(expression);
        checkBrackets(expression);
    }

    private void checkCharacters(String expression) {
        if (!expression.matches("[\\d+\\-*/()\\s]+")) {
            throw new RuntimeException("Expression Error: Expression must contain digits, brackets, operations ('*', '/', '+', '-'), whitespaces only");
        }
    }

    private void checkBrackets(String expression) {
        int difference = 0;
        for (int i = 0; i < expression.length(); i++){
            if (expression.charAt(i) == '('){
                difference++;
            } else if (expression.charAt(i) == ')') {
                if (expression.charAt(i-1) == '(') {
                    throw new RuntimeException("Expression Error: An empty expression in brackets (at position " + i + ").");
                }
                difference--;
            }
            if (difference < 0) {
                throw new RuntimeException("Expression Error: Extra closing bracket at " + i + "position.");
            }
        }
        if (difference > 0) {
            throw new RuntimeException("Expression Error: Extra opening brackets (number of brackets = " + difference +").");
        }
    }

    private static List<Lexeme> lexemesAnalyze(String expText) {
        ArrayList<Lexeme> lexemes = new ArrayList<>();
        int pos = 0;
        while (pos < expText.length()) {
            char c = expText.charAt(pos);
            if (c == '(') {
                lexemes.add(new Lexeme(LexemeType.LEFT_BRACKET, c));
                pos++;
            } else if (c == ')') {
                lexemes.add(new Lexeme(LexemeType.RIGHT_BRACKET, c));
                pos++;
            } else if (c == '+') {
                lexemes.add(new Lexeme(LexemeType.OP_ADD, c));
                pos++;
            } else if (c == '-') {
                lexemes.add(new Lexeme(LexemeType.OP_SUB, c));
                pos++;
            } else if (c == '*') {
                lexemes.add(new Lexeme(LexemeType.OP_MUL, c));
                pos++;
            } else if (c == '/') {
                lexemes.add(new Lexeme(LexemeType.OP_DIV, c));
                pos++;
            } else {
                if (c <= '9' && c >= '0') {
                    StringBuilder digits = new StringBuilder();
                    do {
                        digits.append(c);
                        pos++;
                        if (pos >= expText.length()) {
                            break;
                        }
                        c = expText.charAt(pos);
                    } while (c <= '9' && c >= '0');
                    lexemes.add(new Lexeme(LexemeType.NUMBER, digits.toString()));
                } else {
                    if (c != ' ') {
                        throw new RuntimeException("Unexpected character: " + c);
                    }
                    pos++;
                }
            }
        }
        lexemes.add(new Lexeme(LexemeType.EOF, ""));
        return lexemes;
    }

    private static int calcExpr(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        if (lexeme.type == LexemeType.EOF) {
            return 0;
        } else {
            lexemes.back();
            return calcAddSubExpr(lexemes);
        }
    }

    private static int calcAddSubExpr(LexemeBuffer lexemes) {
        int value = calcMulDivExpr(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_ADD -> value += calcMulDivExpr(lexemes);
                case OP_SUB -> value -= calcMulDivExpr(lexemes);
                case EOF, RIGHT_BRACKET -> {
                    lexemes.back();
                    return value;
                }
                default -> throw new RuntimeException("Unexpected token: " + lexeme.value
                        + " at position: " + lexemes.getPos());
            }
        }
    }

    private static int calcMulDivExpr(LexemeBuffer lexemes) {
        int value = calcFactor(lexemes);
        while (true) {
            Lexeme lexeme = lexemes.next();
            switch (lexeme.type) {
                case OP_MUL -> value *= calcFactor(lexemes);
                case OP_DIV -> value /= calcFactor(lexemes);
                case EOF, RIGHT_BRACKET, OP_ADD, OP_SUB -> {
                    lexemes.back();
                    return value;
                }
                default -> throw new RuntimeException("Unexpected token: " + lexeme.value
                        + " at position: " + lexemes.getPos());
            }
        }
    }

    public static int calcFactor(LexemeBuffer lexemes) {
        Lexeme lexeme = lexemes.next();
        switch (lexeme.type) {
            case NUMBER:
                return Integer.parseInt(lexeme.value);
            case LEFT_BRACKET:
                int value = calcAddSubExpr(lexemes);
                lexeme = lexemes.next();
                if (lexeme.type != LexemeType.RIGHT_BRACKET) {
                    throw new RuntimeException("Unexpected token: " + lexeme.value
                            + " at position: " + lexemes.getPos());
                }
                return value;
            default:
                throw new RuntimeException("Unexpected token: " + lexeme.value
                        + " at position: " + lexemes.getPos());
        }
    }
}
