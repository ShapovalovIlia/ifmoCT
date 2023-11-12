package expression.parser;

public class Main {
    public static void main(String[] args) {
        String expression = "7 * (5-2)";
        ExpressionParser parser = new ExpressionParser();
        System.out.println(parser.parse(expression).evaluate(1, 1, 1));
    }
}
