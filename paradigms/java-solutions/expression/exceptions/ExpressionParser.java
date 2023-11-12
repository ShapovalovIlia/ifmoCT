package expression.exceptions;

import com.sun.jdi.connect.Connector;
import expression.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ExpressionParser implements TripleParser {

    @Override
    public TripleExpression parse(String expression) throws ParseException {
        List<Token> tokens = tokenizate(expression);
        TokenBuffer tokenBuffer = new TokenBuffer(tokens);
        return expr(tokenBuffer);
    }

    static int balance = 0;

    public enum TokenType {
        LEFT_BRACKET, RIGHT_BRACKET,
        PLUS, MINUS, MULTIPLY, DIVISION,
        NUMBER, NAME, VARIABLE, SPACE,
        SET, CLEAR, COUNT,
        EOF;
    }


    public static class Token {
        TokenType type;
        String value;

        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        public Token(TokenType type, Character value) {
            this.type = type;
            this.value = value.toString();
        }

        @Override
        public String toString() {
            return "Token{" +
                    "type=" + type +
                    ", value='" + value + '\'' +
                    '}';
        }
    }

    public static class TokenBuffer {
        private int posBuffer;

        public List<Token> tokens;

        public TokenBuffer(List<Token> tokens) {
            this.tokens = tokens;
        }

        public Token next() {
            return tokens.get(posBuffer++);
        }

        public void back() {
            posBuffer--;
        }

    }

    public static boolean expect(String expText, String text, int pos) {
        for (int i = 0; i < text.length(); i++) {
            if (expText.charAt(pos) != text.charAt(i)) {
                return false;
            }
            pos++;
        }

        return true;
    }

    public static boolean isVariable(char c) {
        return (c == 'x' || c == 'y' || c == 'z' || c == 'X' || c == 'Y' || c == 'Z');
    }

    public static boolean isDigit(char c) {
        return (c <= '9' && c >= '0');
    }

    public static List<Token> tokenizate(String text) throws ParseException {
        ArrayList<Token> tokens = new ArrayList<>();
        int pos = 0;
        while (pos < text.length()) {
            char c = text.charAt(pos);
            switch (c) {
                case '(':
                    tokens.add(new Token(TokenType.LEFT_BRACKET, c));
                    balance++;
                    pos++;
                    continue;
                case ')':
                    tokens.add(new Token(TokenType.RIGHT_BRACKET, c));
                    balance--;
                    pos++;
                    continue;
                case '+':
                    tokens.add(new Token(TokenType.PLUS, c));
                    pos++;
                    continue;
                case '-':
                    if (text.charAt(pos + 1) == ' ') {
                        tokens.add(new Token(TokenType.SPACE, c));
                    } else {
                        tokens.add(new Token(TokenType.MINUS, c));
                    }
                    pos++;
                    continue;
                case '*':
                    tokens.add(new Token(TokenType.MULTIPLY, c));
                    pos++;
                    continue;
                case '/':
                    tokens.add(new Token(TokenType.DIVISION, c));
                    pos++;
                    continue;
                default:
                    if (isDigit(c)) {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(c);
                            pos++;
                            if (pos >= text.length()) {
                                break;
                            }
                            c = text.charAt(pos);
                        } while (isDigit(c));
                        if (pos < text.length() && Character.isLetter(text.charAt(pos))) {
                            balance = 0;
                            throw new ParseException("Letter after number");
                        }
                        tokens.add(new Token(TokenType.NUMBER, sb.toString()));
                    } else {
                        if (!Character.isWhitespace(c)) {
                            if (c == 's' && expect(text, "set", pos)) {
                                tokens.add(new Token(TokenType.SET, "set"));
                                pos += 3;
                                continue;

                            } else if (c == 'c' && expect(text, "clear", pos)) {
                                tokens.add(new Token(TokenType.CLEAR, "clear"));
                                pos += 5;
                                continue;
                            } else if (c == 'c' && expect(text, "count", pos)) {
                                pos += 5;
                                if ((text.charAt(pos) == '(') || (Character.isWhitespace(text.charAt(pos)))) {
                                    tokens.add(new Token(TokenType.COUNT, "count"));
                                    continue;
                                }
                                throw new ParseException("not brackets or whitespace after uno operator");

                            } else if (isVariable(c)) {
                                tokens.add(new Token(TokenType.VARIABLE, c));
                                pos++;
                                continue;
                            }


                            balance = 0;
                            throw new ParseException("Unexpected Character");
                        }
                        pos++;
                    }
            }
        }
        if (balance != 0) {
            balance = 0;
            throw new ParseException("Brackets Mismatch");
        }
        tokens.add(new Token(TokenType.EOF, "\0"));
        return tokens;
    }

    public static CommonExpressions expr(TokenBuffer tokens) throws ParseException {

        return setclear(tokens);
    }

    public static CommonExpressions setclear(TokenBuffer tokens) throws ParseException {
        CommonExpressions value = plusminus(tokens);
        while (true) {
            Token token = tokens.next();
            switch (token.type) {
                case SET -> value = new Set(value, plusminus(tokens));
                case CLEAR -> value = new Clear(value, plusminus(tokens));
                case COUNT -> value = new CheckedCount(value);
                case EOF, PLUS, MINUS, RIGHT_BRACKET -> {
                    tokens.back();
                    return value;
                }
                default -> throw new ParseException("Unknow Error! You shouldn't be there!");
                // или можно кидать AssertionError, потому что мы сюда никогда не попадем
            }
        }
    }

    public static CommonExpressions plusminus(TokenBuffer tokens) throws ParseException {
        CommonExpressions value = multdiv(tokens);
        while (true) {
            Token token = tokens.next();
            switch (token.type) {
                case SPACE, MINUS -> value = new CheckedSubtract(value, multdiv(tokens));
                case PLUS -> value = new CheckedAdd(value, multdiv(tokens));
                case SET, COUNT, RIGHT_BRACKET, CLEAR, EOF -> {
                    tokens.back();
                    return value;
                }

                default -> throw new ParseException("Unknow Error! You shouldn't be there!");
                // или можно кидать AssertionError, потому что мы сюда никогда не попадем
            }
        }
    }

    public static CommonExpressions multdiv(TokenBuffer tokens) throws ParseException {
        CommonExpressions value = factor(tokens);
        while (true) {
            Token token = tokens.next();
            switch (token.type) {
                case MULTIPLY -> value = new CheckedMultiply(value, factor(tokens));
                case DIVISION -> value = new CheckedDivide(value, factor(tokens));
                case EOF, RIGHT_BRACKET, PLUS, MINUS, SPACE, SET, CLEAR, COUNT -> {
                    tokens.back();
                    return value;
                }
                default -> throw new ParseException("Spaces in numbers");
            }
        }
    }

    public static CommonExpressions factor(TokenBuffer tokens) throws ParseException {
        Token token = tokens.next();
        switch (token.type) {
            case SPACE:
                CommonExpressions valueSpace = factor(tokens);
                return new CheckedNegate(valueSpace);
            case MINUS:
                Token nextToken = tokens.next();
                if (nextToken.type == TokenType.NUMBER) {
                    return new Const(Integer.parseInt("-" + nextToken.value));
                }
                tokens.back();
                CommonExpressions value = factor(tokens);
                return new CheckedNegate(value);
            case NUMBER:
                return new Const(Integer.parseInt(token.value));
            case VARIABLE:
                return new Variable(token.value);
            case LEFT_BRACKET:
                value = setclear(tokens);
                token = tokens.next();
                if (token.type != TokenType.RIGHT_BRACKET) {

                    throw new ParseException("Brackets mismatch!");
                }
                return value;

            case COUNT:
                CommonExpressions valueCount = factor(tokens);
                return new CheckedCount(valueCount);
            default:
                throw new ParseException("Incorrect expression");
        }
    }
}