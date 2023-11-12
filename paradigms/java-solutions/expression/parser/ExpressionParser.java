package expression.parser;

import expression.*;

import java.util.ArrayList;
import java.util.List;

public class ExpressionParser implements TripleParser {

    @Override
    public TripleExpression parse(String expression) {
        List<Token> tokens = tokenizate(expression);
        TokenBuffer tokenBuffer = new TokenBuffer(tokens);
        return expr(tokenBuffer);
    }

    public enum TokenType {
        LEFT_BRACKET, RIGHT_BRACKET,
        PLUS, MINUS, MULTIPLY, DIVISION,
        NUMBER, NAME, VARIABLE, SPACE, SET, CLEAR,
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
        private int pos;

        public List<Token> tokens;

        public TokenBuffer(List<Token> tokens) {
            this.tokens = tokens;
        }

        public Token next() {
            return tokens.get(pos++);
        }

        public void back() {
            pos--;
        }


        public int getPos() {
            return pos;
        }
    }

    public static List<Token> tokenizate(String text) {
        ArrayList<Token> tokens = new ArrayList<>();
        int pos = 0;
        while (pos < text.length()) {
            char c = text.charAt(pos);
            switch (c) {
                case '(':
                    tokens.add(new Token(TokenType.LEFT_BRACKET, c));
                    pos++;
                    continue;
                case ')':
                    tokens.add(new Token(TokenType.RIGHT_BRACKET, c));
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
                    if (c <= '9' && c >= '0') {
                        StringBuilder sb = new StringBuilder();
                        do {
                            sb.append(c);
                            pos++;
                            if (pos >= text.length()) {
                                break;
                            }
                            c = text.charAt(pos);
                        } while (c <= '9' && c >= '0');
                        tokens.add(new Token(TokenType.NUMBER, sb.toString()));
                    } else {
                        if (!Character.isWhitespace(c)) {
                            if (c == 'x' || c == 'y' || c == 'z') {
                                tokens.add(new Token(TokenType.VARIABLE, c));
                                pos++;
                                continue;
                            } else if (c == 's' && text.charAt(pos + 1) == 'e' && text.charAt(pos + 2) == 't') {
                                tokens.add(new Token(TokenType.SET, "set"));
                                pos += 3;
                                continue;
                            } else if (c == 'c' && text.charAt(pos + 1) == 'l' && text.charAt(pos + 2) == 'e'
                                    && text.charAt(pos + 3) == 'a' && text.charAt(pos + 4) == 'r') {
                                tokens.add(new Token(TokenType.CLEAR, "clear"));
                                pos += 5;
                                continue;
                            }
                            throw new RuntimeException("Unexpected character: " + c);
                        }
                        pos++;
                    }
            }
        }
        tokens.add(new Token(TokenType.EOF, "\0"));
        return tokens;
    }

    public static CommonExpressions expr(TokenBuffer tokens) {

        return setclear(tokens);
    }

    public static CommonExpressions setclear(TokenBuffer tokens) {
        CommonExpressions value = plusminus(tokens);
        while (true) {
            Token token = tokens.next();
            switch (token.type) {
                case SET -> value = new Set(value, plusminus(tokens));
                case CLEAR -> value = new Clear(value, plusminus(tokens));
                case EOF, RIGHT_BRACKET, PLUS, MINUS -> {
                    tokens.back();
                    return value;
                }
                default -> throw new RuntimeException("Unexpected token: " + token.value
                        + " at position: " + tokens.getPos());
            }
        }
    }

    public static CommonExpressions plusminus(TokenBuffer tokens) {
        CommonExpressions value = multdiv(tokens);
        while (true) {
            Token token = tokens.next();
            switch (token.type) {
                case SPACE, MINUS -> value = new Subtract(value, multdiv(tokens));
                case PLUS -> value = new Add(value, multdiv(tokens));
                case SET, CLEAR, EOF, RIGHT_BRACKET -> {
                    tokens.back();
                    return value;
                }
                default -> throw new RuntimeException("Unexpected token: " + token.value
                        + " at position: " + tokens.getPos());
            }
        }
    }

    public static CommonExpressions multdiv(TokenBuffer tokens) {
        CommonExpressions value = factor(tokens);
        while (true) {
            Token token = tokens.next();
            switch (token.type) {
                case MULTIPLY -> value = new Multiply(value, factor(tokens));
                case DIVISION -> value = new Divide(value, factor(tokens));
                case EOF, RIGHT_BRACKET, PLUS, MINUS, SPACE, SET, CLEAR -> {
                    tokens.back();
                    return value;
                }
                default -> throw new RuntimeException("Unexpected token: " + token.value
                        + " at position: " + tokens.getPos());
            }
        }
    }

    public static CommonExpressions factor(TokenBuffer tokens) {
        Token token = tokens.next();
        switch (token.type) {
            case SPACE:
                CommonExpressions valueSpace = factor(tokens);
                return new Negate(valueSpace);
            case MINUS:
                Token nextToken = tokens.next();
                if (nextToken.type == TokenType.NUMBER) {
                    return new Const(Integer.parseInt("-" + nextToken.value));
                }
                tokens.back();
                CommonExpressions value = factor(tokens);
                return new Negate(value);
            case NUMBER:
                return new Const(Integer.parseInt(token.value));
            case VARIABLE:
                return new Variable(token.value);
            case LEFT_BRACKET:
                value = setclear(tokens);
                token = tokens.next();
                if (token.type != TokenType.RIGHT_BRACKET) {

                    throw new RuntimeException("Unexpected token: " + token.value
                            + " at position: " + tokens.getPos());
                }
                return value;
            default:
                throw new RuntimeException("Unexpected token: " + token.value
                        + " at position: " + tokens.getPos());
        }
    }
}