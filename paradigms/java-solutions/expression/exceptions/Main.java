package expression.exceptions;


public class Main {
    public static void main(String[] args)  {
        ExpressionParser parser = new ExpressionParser();
        String expression = "0";
        try {
            System.out.println(parser.parse(expression));
        } catch (ParseException e) {
            System.out.println(e.getMessage());
        }
    }
}
